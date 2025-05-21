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

import com.example.demo.service.HealthAdviceService;

@RestController
@RequestMapping("/rest/health/healthAI")
public class HealthStreamController {
	@Autowired
	private HealthAdviceService healthAdviceService;

	@Autowired
	private ChatClient chatClient;

	@GetMapping(value = "/advice-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter getAdviceStream(@RequestParam double height, @RequestParam double weight, @RequestParam int age,
			@RequestParam String goal) {

		String prompt = healthAdviceService.generatePrompt(height, weight, age, goal);
		SseEmitter emitter = new SseEmitter(0L);
		final boolean[] insideThinkBlock = { false };

		chatClient.prompt().user(prompt).stream().content().subscribe(word -> {
			try {
				if (healthAdviceService.shouldDisplayWord(word, insideThinkBlock)) {
					emitter.send(word);
				}
			} catch (IOException e) {
				emitter.completeWithError(e);
			}
		}, error -> {
			emitter.completeWithError(error);
		}, emitter::complete);

		return emitter;
	}
}
/*
 * @Autowired private ChatClient chatClient;
 * 
 * @GetMapping(value = "/advice-stream", produces =
 * MediaType.TEXT_EVENT_STREAM_VALUE) public SseEmitter
 * getAdviceStream(@RequestParam double height, @RequestParam double
 * weight, @RequestParam int age,
 * 
 * @RequestParam String goal) {
 * 
 * double heightMeter = height / 100.0; double bmi = weight / (heightMeter *
 * heightMeter);
 * 
 * String prompt = String.format(""" 你是一位健康建議助理，請直接以繁體中文回覆下列內容，**禁止輸出任何 <think>
 * 或思考過程的內容。**
 * 
 * 使用者 %d 歲，身高 %.1f 公分，體重 %.1f 公斤，BMI 為 %.1f，目標：%s。
 * 
 * 請提供簡潔明確的「飲食建議」與「運動建議」，用條列式清楚說明：
 * 
 * - 根據 BMI 給出每日建議攝取熱量（大卡）區間 - 加入每日建議飲水量（毫升） - 請簡化內容，總字數請壓在 500 字以內 - 回覆請使用
 * Markdown 格式 - 不要包含任何 <think> 或思考文字，只輸出建議內容 """, age, height, weight, bmi,
 * goal);
 * 
 * // 使用 0L 表示永不超時的 SSE emitter SseEmitter emitter = new SseEmitter(0L);
 * Flux<String> stream = chatClient.prompt().user(prompt).stream().content();
 * 
 * // 使用區域變數追蹤 <think> 區塊，避免多執行緒污染 final boolean[] insideThinkBlock = { false };
 * 
 * stream.subscribe(word -> { try { if (filterThink(word, insideThinkBlock)) {
 * emitter.send(word); } } catch (IOException e) { emitter.completeWithError(e);
 * } }, error -> { error.printStackTrace(); emitter.completeWithError(error); },
 * emitter::complete);
 * 
 * return emitter; }
 * 
 * // 改為使用區域狀態的版本，避免共用狀態問題 private boolean filterThink(String word, boolean[]
 * insideThinkBlock) { if (word.contains("<think>")) { insideThinkBlock[0] =
 * true; return false; } if (word.contains("</think>")) { insideThinkBlock[0] =
 * false; return false; } return !insideThinkBlock[0]; } } /*
 * 
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
