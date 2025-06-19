package com.example.demo.service;

//Email 發送服務介面（用於註冊驗證信、忘記密碼通知等）
public interface EmailService {

	// 發送驗證信或通知信
	// @param to 收件人 Email 地址
	// @param confirmUrl 驗證或操作的連結（通常為後端產生的 URL）
	void sendEmail(String to, String confirmUrl);
}
