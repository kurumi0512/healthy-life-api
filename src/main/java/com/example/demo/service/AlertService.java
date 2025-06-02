package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	/**
	 * 傳送血糖異常提醒給使用者
	 * 
	 * @param username 使用者帳號名稱（來自 Principal）
	 * @param message  要推播的訊息
	 */
	public void sendBloodSugarWarning(Integer userId, String message) {
		System.out.println("📢 發送推播給使用者 ID：" + userId + "，訊息：" + message);
		messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/alerts", message);
	}
}