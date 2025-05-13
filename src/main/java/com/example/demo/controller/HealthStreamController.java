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
	public SseEmitter getAdviceStream(@RequestParam double height, @RequestParam double weight, @RequestParam int age) {
		double heightMeter = height / 100.0;
		double bmi = weight / (heightMeter * heightMeter);

		String prompt = String.format("使用者年齡為 %d 歲，身高為 %.1f 公分，體重為 %.1f 公斤，BMI 為 %.1f。請根據這些資訊，提供飲食與運動建議（分段、條列、具體可執行）。",
				age, height, weight, bmi);

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