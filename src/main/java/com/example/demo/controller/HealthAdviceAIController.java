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

		chatClient.prompt()
				.system("""
						你是一位健康建議助理，請直接以繁體中文回覆下列內容，**禁止輸出任何 <think> 或思考過程的內容。**

								使用者 %d 歲，身高 %.1f 公分，體重 %.1f 公斤，BMI 為 %.1f，目標：%s。

								請提供簡潔明確的「飲食建議」與「運動建議」，回覆請依照以下格式列出，每一段開頭請加上「•」，段落之間請換行（不要連在一起），內容請使用 Markdown 格式，總字數限制在 500 字以內，語氣請自然、像台灣人在說話。

								請依以下順序提供內容：
								1. 根據 BMI 給出每日建議攝取熱量（大卡）區間
								2. 建議每日飲水量（毫升）
								3. 飲食建議：列出幾項主要原則與建議食材（包含澱粉、蛋白質、蔬菜等）
								4. 運動建議：提供簡單、好執行的日常運動與建議時間

								**請只輸出建議內容，不要加入 <think>、開場白、或其他說明**

						""")
				.user(prompt).stream().content().subscribe(message -> { // 用 Spring AI 的 ChatClient 呼叫 AI
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
