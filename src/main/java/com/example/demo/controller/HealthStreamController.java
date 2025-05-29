package com.example.demo.controller;

import java.io.IOException;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.demo.model.dto.UserCert;
import com.example.demo.model.entity.User;
import com.example.demo.repository.AdviceHistoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.HealthAdviceService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/rest/health/healthAI")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class HealthStreamController {

	@Autowired
	private HealthAdviceService healthAdviceService;

	@Autowired
	private AdviceHistoryRepository adviceHistoryRepository;

	@Autowired
	private ChatClient chatClient;

	@Autowired
	private UserRepository userRepository;

	@GetMapping(value = "/advice-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter getAdviceStream(@RequestParam double height, @RequestParam double weight, @RequestParam int age,
			@RequestParam String goal, HttpSession session) {

		String prompt = healthAdviceService.generatePrompt(height, weight, age, goal);
		SseEmitter emitter = new SseEmitter(0L); // 無限等待時間
		final boolean[] insideThinkBlock = { false };

		// 用來累積完整建議文字
		StringBuilder fullAdvice = new StringBuilder();

		chatClient.prompt().user(prompt).stream().content().subscribe(message -> {
			try {
				System.out.println("✅ AI 回傳片段：" + message);

				if (message != null && !message.trim().isEmpty()
						&& healthAdviceService.shouldDisplayWord(message, insideThinkBlock)) {

					// 🛡️ 過濾 AI 偷輸出的開場白或雜訊
					String cleanMsg = message.trim();
					if (cleanMsg.startsWith("或多餘的文字") || cleanMsg.startsWith("或思考过程，直接输出建议")
							|| cleanMsg.toLowerCase().contains("<think>")
							|| cleanMsg.toLowerCase().startsWith("讓我們一起來看看")
							|| cleanMsg.toLowerCase().startsWith("這是一個健康建議")) {
						System.out.println("🛑 過濾雜訊片段：" + cleanMsg);
						return; // 不送到前端
					}

					fullAdvice.append(cleanMsg);
					emitter.send(cleanMsg);
				}
			} catch (IOException e) {
				emitter.completeWithError(e);
			}
		}, error -> {
			emitter.completeWithError(error);
		}, () -> {
			try {
				emitter.send("[DONE]");
				emitter.complete();

				// ✅ 串流完成後儲存到資料庫
				UserCert cert = (UserCert) session.getAttribute("cert");
				if (cert != null) {
					User user = userRepository.findByAccount_Id(cert.getAccountId()).orElse(null);
					if (user != null) {
						healthAdviceService.saveAdviceRecord(user.getId(), prompt, fullAdvice.toString());
					} else {
						System.out.println("⚠️ 找不到對應的使用者");
					}
				} else {
					System.out.println("⚠️ 無登入資訊，略過儲存建議紀錄");
				}
			} catch (IOException e) {
				emitter.completeWithError(e);
			}
		});
		return emitter;
	}
}
