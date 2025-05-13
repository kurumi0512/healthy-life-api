package com.example.demo.controller;

import java.io.IOException;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/health")
public class HealthStreamController {

	@Autowired
	private ChatClient chatClient;

	@GetMapping(value = "/advice-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter getAdviceStream(@RequestParam double height, @RequestParam double weight, @RequestParam int age,
			@RequestParam String goal) {
		double heightMeter = height / 100.0;
		double bmi = weight / (heightMeter * heightMeter);

		String prompt = String.format("""
				使用者 %d 歲，身高 %.1f 公分，體重 %.1f 公斤，BMI 為 %.1f，目標：%s。
				請提供完整的「飲食建議」與「運動建議」，用條列式清楚說明。
				""", age, height, weight, bmi, goal);

		SseEmitter emitter = new SseEmitter();
		Flux<String> stream = chatClient.prompt().user(prompt).stream().content();

		stream.subscribe(word -> {
			try {
				emitter.send(word);
			} catch (IOException e) {
				emitter.completeWithError(e);
			}
		}, emitter::completeWithError, emitter::complete);

		return emitter;
	}
}