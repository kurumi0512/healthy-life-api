package com.example.demo.controller;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/rest/health/news")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class NewsRecommendationController {

	@Autowired
	private ChatClient chatClient; // Spring AI 的 ChatClient（用來呼叫 AI）

	// [GET] AI 推薦一則健康新聞標題，並說明原因
	@GetMapping("/recommend")
	public ResponseEntity<Map<String, String>> recommendNews() {

		// 要求 AI 從多個健康新聞標題中選出最推薦的，並說明原因
		String prompt = """
				    請從下列健康新聞標題中選出一篇你最推薦的，並說明推薦理由。

				    - 小吃店常見青菜是美國營養師首選！
				    - 經常脹氣怎麼辦？這些低FODMAP食品可多吃！
				    - 自助餐夾這些菜是高油熱量炸彈！
				    - 為何蛋白那麼重要?
				    - 延緩老化吃木瓜！營養師推4水果：助控血糖、也助眠

				    請僅回傳以下 JSON 格式（不要加上 ```json 或其他文字）：
				    {"title": "xxx", "reason": "yyy"}
				""";

		// 呼叫 AI 並取得純文字回覆
		String response = chatClient.prompt().user(prompt).call().content();

		// 移除可能夾雜的 ```json 或 ``` 符號（避免 JSON 解析錯誤）
		String cleaned = response.replaceAll("```json", "").replaceAll("```", "").trim();

		System.out.println("清理後的 AI 回傳內容:\n" + cleaned);

		try {
			// 將 AI 回傳的 JSON 字串轉成 Map
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = mapper.readValue(cleaned, new TypeReference<>() {
			});

			// 根據標題補上對應的新聞連結（用 Map 對應）
			String title = map.get("title");
			Map<String, String> titleToUrl = Map.of("小吃店常見青菜是美國營養師首選！", "https://www.edh.tw/article/37784",
					"經常脹氣怎麼辦？這些低FODMAP食品可多吃！", "https://www.edh.tw/article/29956", "自助餐夾這些菜是高油熱量炸彈！",
					"https://www.edh.tw/article/37435", "為何蛋白那麼重要?", "http://www.healthnews.com.tw/article/65035", // 用
																													// http
																													// 避開憑證錯誤
					"延緩老化吃木瓜！營養師推4水果：助控血糖、也助眠", "https://heho.com.tw/archives/352");
			String url = titleToUrl.getOrDefault(title, ""); // 若找不到就給空字串
			map.put("url", url); // 把 URL 加進回傳資料裡

			return ResponseEntity.ok(map); // 回傳包含 title、reason、url 的 JSON
		} catch (Exception e) {
			System.err.println("JSON 解析失敗: " + e.getMessage());
			return ResponseEntity.badRequest().body(Map.of("error", "AI 回傳格式錯誤"));
		}
	}
}