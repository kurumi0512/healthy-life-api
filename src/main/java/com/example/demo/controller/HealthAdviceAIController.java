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

	@Autowired // Spring AI 的聊天元件（串接 AI）
	private ChatClient chatClient;

	@Autowired
	private UserRepository userRepository;

	// [GET] 串流取得 AI 健康建議（Server-Sent Events 格式）
	// 前端會逐句接收到建議內容 → 可即時顯示在畫面上
	// 產生完成後會自動儲存建議到資料庫
	@GetMapping(value = "/advice-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter getAdviceStream(@RequestParam double height, @RequestParam double weight, @RequestParam int age,
			@RequestParam String goal, @RequestParam(required = false) String mode, HttpSession session) {

		// 產生 prompt：如果沒帶 mode，就用預設的完整建議；否則依 mode 模式產生
		String prompt = (mode == null || mode.isBlank()) ? healthAdviceService.generatePrompt(height, weight, age, goal)
				: healthAdviceService.generatePrompt(height, weight, age, goal, mode);

		SseEmitter emitter = new SseEmitter(0L); // 0L 表示永不超時
		final boolean[] insideThinkBlock = { false }; // 用來判斷 AI 是否進入 <think> 區段

		// 用來累積完整建議文字
		StringBuilder fullAdvice = new StringBuilder(); // 累積整段建議內容（用來儲存）

		// 呼叫 AI 並啟用串流回覆
		chatClient.prompt().user(prompt).stream().content().subscribe(message -> { // 用 Spring AI 的 ChatClient 呼叫 AI
			// .stream().content().subscribe(...)：進入 串流模式，逐句接收 AI 的回覆。
			try {
				System.out.println("AI 回傳片段：" + message);

				// 檢查是否應該顯示這段訊息（例如排除 <think> 區塊）
				if (message != null && !message.trim().isEmpty()
						&& healthAdviceService.shouldDisplayWord(message, insideThinkBlock)) {

					// 美化訊息：加換行讓段落清晰
					String cleanMsg = message.trim().replace("•", "\n•") // 小黑點前加換行
							.replaceAll("\n", "\n\n"); // 將單換行變雙換行讓段落更清晰

					fullAdvice.append(cleanMsg); // 累積完整建議內容
					emitter.send(cleanMsg); // 推送給前端顯示
				}
			} catch (IOException e) {
				emitter.completeWithError(e); // 若出錯，終止串流
			}
		}, error -> {
			emitter.completeWithError(error); // 若 AI 發生錯誤，終止串流
		}, () -> {
			try {
				emitter.send("[DONE]"); // 通知前端串流已完成
				emitter.complete(); // 關閉串流

				// 串流完成後，儲存建議紀錄
				UserCert cert = (UserCert) session.getAttribute("cert"); // 從 session 取得登入資訊
				if (cert != null) {
					User user = userRepository.findByAccount_Id(cert.getAccountId()).orElse(null);
					if (user != null) {
						// 儲存 prompt 與 AI 回覆
						healthAdviceService.saveAdviceRecord(user.getId(), prompt, fullAdvice.toString());
					} else {
						System.out.println("找不到對應的使用者");
					}
				} else {
					System.out.println("無登入資訊，略過儲存建議紀錄");
				}
			} catch (IOException e) {
				emitter.completeWithError(e);
			}
		});
		return emitter;
	}

	// [GET] 取得目前使用者的最新一筆 AI 健康建議（for 歷史紀錄）
	@GetMapping("/history/latest")
	public ApiResponse<?> getLatestAdvice(HttpSession session) {
		UserCert cert = (UserCert) session.getAttribute("cert");
		if (cert == null) {
			return ApiResponse.error("未登入，無法取得歷史建議");
		}

		// 根據帳號 ID 找到對應的使用者
		User user = userRepository.findByAccount_Id(cert.getAccountId()).orElse(null);
		if (user == null) {
			return ApiResponse.error("找不到對應的使用者");
		}

		// 查詢該使用者最新一筆建議紀錄
		AdviceHistory latest = adviceHistoryRepository.findTopByUser_IdOrderByCreatedAtDesc(user.getId());

		if (latest == null) {
			return ApiResponse.success("尚無建議紀錄", null);
		}

		// 回傳建議內容：包含使用者輸入的 prompt、AI 回覆、與產生時間
		return ApiResponse.success("查詢成功", Map.of("inputContext", latest.getInputContext(), "generatedAdvice",
				latest.getGeneratedAdvice(), "createdAt", latest.getCreatedAt()));
	}
}
