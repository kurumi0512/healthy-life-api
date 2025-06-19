package com.example.demo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 標示這是一個 Spring 的組態類別，會被容器掃描並建立 Bean
public class ChatConfig {

	// 建立 ChatClient Bean，供 AI 建議用（如 HealthAdviceAIController 注入使用）

	@Bean
	public ChatClient chatClient(ChatClient.Builder builder) {
		return builder.build(); // 將設定完成的 Builder 組裝成 ChatClient 實體
	}

}
