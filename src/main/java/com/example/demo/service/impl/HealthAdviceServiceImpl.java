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
	private UserRepository userRepository;

	@Autowired
	private AdviceHistoryRepository adviceHistoryRepository;

	@Autowired
	private ChatClient chatClient;

	// 預熱模型，避免第一次超慢
	@PostConstruct
	public void warmUpModel() {
		try {
			chatClient.prompt().user("請用繁體中文回覆：你好").call();
			System.out.println("✅ AI 模型預熱完成");
		} catch (Exception e) {
			System.err.println("⚠️ 模型預熱失敗：" + e.getMessage());
		}
	}

	@Override
	public String generatePrompt(double height, double weight, int age, String goal) {
		double bmi = weight / Math.pow(height / 100.0, 2);
		return String.format("""
				     		你是一位健康建議助理，請直接以繁體中文回覆下列內容，**禁止輸出任何 <think> 或思考過程的內容。**

				使用者 %d 歲，身高 %.1f 公分，體重 %.1f 公斤，BMI 為 %.1f，目標：%s。

				請提供簡潔明確的「飲食建議」與「運動建議」，用條列式清楚說明：

				- 根據 BMI 給出每日建議攝取熱量（大卡）區間
				- 加入每日建議飲水量（毫升）
				- 請簡化內容，總字數請壓在 500 字以內
				- 回覆請使用 Markdown 格式
				- 不要包含任何 <think> 或思考文字，只輸出建議內容
				""", age, height, weight, bmi, goal); // 原始 prompt 字串
	}

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

	@Override
	public void saveAdviceRecord(Integer userId, String input, String advice) {
		User user = userRepository.findById(userId).orElse(null);
		if (user == null)
			return;
		saveAdviceRecord(user, input, advice); // ✅ 呼叫上面那個方法
	}

	public void saveAdviceRecord(User user, String prompt, String generatedAdvice) {
		AdviceHistory history = new AdviceHistory();
		history.setUser(user);
		history.setInputContext(prompt);
		history.setGeneratedAdvice(generatedAdvice);
		history.setType("AI健康建議");
		history.setCreatedAt(LocalDateTime.now()); // ➕ 時間
		adviceHistoryRepository.save(history);
	}

}
