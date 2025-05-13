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

				請提供簡潔明確的「飲食建議」與「運動建議」，用條列式清楚說明：
				- 根據 BMI 給出每日建議攝取熱量（大卡）區間
				- 加入每日建議飲水量（毫升）
				- 請簡化內容，總字數請壓在 500 字以內
				- 回覆請使用「繁體中文」
				- 不用回覆 <think> 的內容
				- 回覆格式請使用 Markdown，包含適當的標題與子標題
				""", age, height, weight, bmi, goal);

		SseEmitter emitter = new SseEmitter(0L);
		Flux<String> stream = chatClient.prompt().user(prompt).stream().content();

		stream.subscribe(word -> {
			try {
				emitter.send(word);
			} catch (IOException e) {
				emitter.completeWithError(e);
			}
		}, error -> {
			error.printStackTrace(); // log 錯誤原因
			emitter.completeWithError(error);
		}, emitter::complete);

		return emitter;
	}

	/*
	 * @GetMapping(value = "/advice", produces = MediaType.TEXT_PLAIN_VALUE) public
	 * ResponseEntity<Map<String, String>> getAdvice(@RequestParam double
	 * height, @RequestParam double weight,
	 * 
	 * @RequestParam int age, @RequestParam String goal) { double heightMeter =
	 * height / 100.0; double bmi = weight / (heightMeter * heightMeter);
	 * 
	 * SseEmitter emitter = new SseEmitter(0L); String prompt = String.format("""
	 * 使用者 %d 歲，身高 %.1f 公分，體重 %.1f 公斤，BMI 為 %.1f，目標：%s。
	 * 請提供完整的「飲食建議」與「運動建議」，用條列式清楚說明。 """, age, height, weight, bmi, goal);
	 * 
	 * try { String advice = chatClient.prompt().user(prompt).call().content();
	 * 
	 * return ResponseEntity.ok(Map.of("advice", advice));
	 * 
	 * } catch (Exception e) { e.printStackTrace(); // log 錯誤 return
	 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error",
	 * "取得建議時發生錯誤，請稍後再試")); } }
	 */
}
