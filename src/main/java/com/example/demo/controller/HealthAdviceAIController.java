package com.example.demo.controller;

import java.io.IOException;
import java.util.Map;

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
import com.example.demo.model.entity.AdviceHistory;
import com.example.demo.model.entity.User;
import com.example.demo.repository.AdviceHistoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.HealthAdviceService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/rest/health/healthAI")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class HealthAdviceAIController {

	@Autowired
	private HealthAdviceService healthAdviceService;

	@Autowired
	private AdviceHistoryRepository adviceHistoryRepository;

	@Autowired // 註冊的 ChatClient
	private ChatClient chatClient;

	@Autowired
	private UserRepository userRepository;

	// 告訴前端這是「SSE串流格式」的回應
	// 單向串流通訊技術，讓「伺服器主動把資料持續推送給前端瀏覽器
	@GetMapping(value = "/advice-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter getAdviceStream(@RequestParam double height, @RequestParam double weight, @RequestParam int age,
			@RequestParam String goal, @RequestParam(defaultValue = "goal") String mode, // ✅ 新增
			HttpSession session) {

		String prompt = healthAdviceService.generatePrompt(height, weight, age, goal, mode);

		SseEmitter emitter = new SseEmitter(0L); // 0L 表示永不超時
		final boolean[] insideThinkBlock = { false };

		// 用來累積完整建議文字
		StringBuilder fullAdvice = new StringBuilder();

		chatClient.prompt().user(prompt).stream().content().subscribe(message -> { // 用 Spring AI 的 ChatClient 呼叫 AI
			// .stream().content().subscribe(...)：進入 串流模式，逐句接收 AI 的回覆。
			try {
				System.out.println("✅ AI 回傳片段：" + message);

				if (message != null && !message.trim().isEmpty()
						&& healthAdviceService.shouldDisplayWord(message, insideThinkBlock)) {

					// 過濾 AI 偷輸出的開場白或雜訊
					String cleanMsg = message.trim().replace("•", "\n•") // 小黑點前加換行
							.replaceAll("\n", "\n\n"); // 將單換行變雙換行讓段落更清晰

					fullAdvice.append(cleanMsg); // 加入完整建議內容
					emitter.send(cleanMsg); // 送出這段格式化後的內容到前端
				}
			} catch (IOException e) {
				emitter.completeWithError(e);
			}
		}, error -> {
			emitter.completeWithError(error);
		}, () -> {
			try {
				emitter.send("[DONE]"); // 表示串流結束，通知前端不再送新資料
				emitter.complete();

				// 串流完成後儲存到資料庫
				UserCert cert = (UserCert) session.getAttribute("cert");
				if (cert != null) {
					User user = userRepository.findByAccount_Id(cert.getAccountId()).orElse(null);// 從 Session
																									// 取出登入使用者資訊（UserCert
																									// 是自訂的登入資訊）
					if (user != null) {
						healthAdviceService.saveAdviceRecord(user.getId(), prompt, fullAdvice.toString()); // 將建議內容（prompt
																											// +
																											// 回覆）儲存進資料庫，對應到使用者
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

	@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
	@GetMapping("/history/latest")
	public ApiResponse<?> getLatestAdvice(HttpSession session) {
		UserCert cert = (UserCert) session.getAttribute("cert");
		if (cert == null) {
			return ApiResponse.error("未登入，無法取得歷史建議");
		}

		// 用 accountId 找到對應的 User（注意不是用 cert.getAccountId() 當 userId）
		User user = userRepository.findByAccount_Id(cert.getAccountId()).orElse(null);
		if (user == null) {
			return ApiResponse.error("找不到對應的使用者");
		}

		// 用正確 user.id 查詢歷史紀錄
		AdviceHistory latest = adviceHistoryRepository.findTopByUser_IdOrderByCreatedAtDesc(user.getId());

		if (latest == null) {
			return ApiResponse.success("尚無建議紀錄", null);
		}

		return ApiResponse.success("查詢成功", Map.of("inputContext", latest.getInputContext(), "generatedAdvice",
				latest.getGeneratedAdvice(), "createdAt", latest.getCreatedAt()));
	}
}
