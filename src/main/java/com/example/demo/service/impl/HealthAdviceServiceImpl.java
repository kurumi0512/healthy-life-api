package com.example.demo.service.impl;

import java.time.LocalDateTime;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.entity.AdviceHistory;
import com.example.demo.model.entity.User;
import com.example.demo.repository.AdviceHistoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.HealthAdviceService;

import jakarta.annotation.PostConstruct;

@Service
public class HealthAdviceServiceImpl implements HealthAdviceService {

	@Autowired
	private UserRepository userRepository; // 操作使用者資料表

	@Autowired
	private AdviceHistoryRepository adviceHistoryRepository; // 操作建議紀錄資料表

	@Autowired
	private ChatClient chatClient; // Spring AI 的聊天客戶端

	// 啟動時預先呼叫 AI 模型一次，避免第一次使用很慢
	@PostConstruct
	public void warmUpModel() {
		try {
			chatClient.prompt().user("請用繁體中文回覆：你好").call();
			System.out.println("AI 模型預熱完成");
		} catch (Exception e) {
			System.err.println("模型預熱失敗：" + e.getMessage());
		}
	}

	// 根據使用者輸入參數組成完整 prompt 給 AI（完整建議模式）
	@Override
	public String generatePrompt(double height, double weight, int age, String goal) {
		double bmi = weight / Math.pow(height / 100.0, 2);
		return String.format(
				"""
						你是一位健康建議助理，請直接以繁體中文回覆下列內容，**禁止輸出任何 <think> 或思考過程的內容。**

						使用者 %d 歲，身高 %.1f 公分，體重 %.1f 公斤，BMI 為 %.1f，目標：%s。

						請提供簡潔明確的「飲食建議」與「運動建議」，回覆請依照以下格式列出，每一段開頭請加上「•」，段落之間請換行（不要連在一起），內容請使用 Markdown 格式，總字數限制在 500 字以內，語氣請自然、像台灣人在說話。

						請依以下順序提供內容：
						1. 根據 BMI 給出每日建議攝取熱量（大卡）區間
						2. 建議每日飲水量（毫升）
						3. 飲食建議：列出幾項主要原則與建議食材（包含澱粉、蛋白質、蔬菜等）
						4. 運動建議：提供簡單、好執行的日常運動與建議時間

						**請只輸出建議內容，不要加入 <think>、開場白、或其他說明**
						""",
				age, height, weight, bmi, goal);
	}

	// 依不同模式（diet/exercise/goal）組 prompt
	@Override
	public String generatePrompt(double height, double weight, int age, String goal, String mode) {
		double bmi = weight / Math.pow(height / 100.0, 2);
		String baseInfo = String.format("使用者 %d 歲，身高 %.1f 公分，體重 %.1f 公斤，BMI 為 %.1f。", age, height, weight, bmi);

		return switch (mode) {
		case "diet" -> String.format("""
				你是一位營養師，請用繁體中文回覆以下健康問題：
				%s
				請給予每日飲食建議，列出幾個原則，限制 200 字以內。
				""", baseInfo);

		case "exercise" -> String.format("""
				你是一位健身教練，請用繁體中文回覆以下健康問題：
				%s
				請給予日常運動建議，包括建議的類型與時間安排，限制 200 字以內。
				""", baseInfo);

		default -> String.format("""
				請用繁體中文提供清楚、簡潔的健康建議，字數不超過 200 字。
				%s
				使用者希望達成的目標是「%s」。
				""", baseInfo, goal);
		};
	}

	// 過濾 <think> 思考區塊中的字，不顯示在畫面上
	@Override
	public boolean shouldDisplayWord(String word, boolean[] insideThinkBlock) {
		if (word.contains("<think>")) {
			insideThinkBlock[0] = true;
			return false;
		}
		if (word.contains("</think>")) {
			insideThinkBlock[0] = false;
			return false;
		}
		return !insideThinkBlock[0];
	}

	// 儲存健康建議紀錄（用 userId 查找 User）
	@Override
	public void saveAdviceRecord(Integer userId, String input, String advice) {
		User user = userRepository.findById(userId).orElse(null);
		if (user == null)
			return;
		saveAdviceRecord(user, input, advice);
	}

	// 儲存健康建議紀錄（用 User 實體）
	public void saveAdviceRecord(User user, String prompt, String generatedAdvice) {
		AdviceHistory history = new AdviceHistory();
		history.setUser(user);
		history.setInputContext(prompt);
		history.setGeneratedAdvice(generatedAdvice);
		history.setType("AI健康建議");
		history.setCreatedAt(LocalDateTime.now()); // 建議生成時間
		adviceHistoryRepository.save(history);
	}

	// 呼叫 AI 並取得建議（完整模式）
	@Override
	public String getAdvice(double height, double weight, int age, String goal) {
		String prompt = generatePrompt(height, weight, age, goal);
		return chatClient.prompt().user(prompt).call().content();
	}

	// 呼叫 AI 並取得建議（模式版）
	@Override
	public String getAdvice(double height, double weight, int age, String goal, String mode) {
		String prompt = generatePrompt(height, weight, age, goal, mode);
		return chatClient.prompt().user(prompt).call().content();
	}

}