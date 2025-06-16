package com.example.demo.service.impl;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.service.EmailService;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

	private final String googleAppPassword = "ydgw dfai heiz qxqa";
	private final String from = "foreverlove0512t@gmail.com";

	@Value("${app.base-url}")
	private String baseUrl; // ✅ 自動注入網址

	@Override
	public void sendEmail(String to, String username) {
		String confirmUrl = baseUrl + "/rest/health/email/confirm?username=" + username;

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, googleAppPassword);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject("《體重與健康AI追蹤系統》會員註冊確認信");

			String html = buildVerificationEmail(username, confirmUrl);
			System.out.println("📧 信件內容 HTML：\n" + html);
			message.setContent(html, "text/html; charset=utf-8");

			Transport.send(message);
			System.out.println("✅ 驗證信寄出：" + to);
		} catch (MessagingException e) {
			System.out.println("❌ 寄送失敗：" + e.getMessage());
		}
	}

	// ✅ 客製化信件樣板
	private String buildVerificationEmail(String username, String confirmUrl) {
		return """
				<!DOCTYPE html>
				<html lang="zh-TW">
				<head>
				  <meta charset="UTF-8" />
				  <title>會員驗證信</title>
				</head>
				<body style="font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;">
				  <div style="max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
				    <h2 style="color: #2e7d32;">歡迎加入《體重與健康 AI 追蹤系統》</h2>
				    <p>親愛的會員，您好：</p>
				    <p>感謝您註冊本系統！為了完成帳號啟用，請點擊下方連結：</p>
				    <p><a href="%s" style="color: #1e88e5;">%s</a></p>

				    <p style="margin-top: 30px; font-size: 0.9rem; color: #777;">體重與健康 AI 追蹤系統 團隊</p>
				  </div>
				</body>
				</html>
				"""
				.formatted(confirmUrl, confirmUrl); // ✅ 傳入兩次 confirmUrl（超連結 href 和文字）

	}

	// EmailServiceImpl.java 補充方法
	public void sendResetCode(String to, String code) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, googleAppPassword);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject("《體重與健康AI追蹤系統》忘記密碼驗證碼");

			String html = buildResetCodeEmail(code);
			message.setContent(html, "text/html; charset=utf-8");

			Transport.send(message);
			System.out.println("✅ 忘記密碼驗證碼已寄出：" + to);
		} catch (MessagingException e) {
			System.out.println("❌ 寄送失敗：" + e.getMessage());
		}
	}

	private String buildResetCodeEmail(String code) {
		return """
				<!DOCTYPE html>
				<html lang=\"zh-TW\">
				<head>
				  <meta charset=\"UTF-8\" />
				  <title>忘記密碼驗證碼</title>
				</head>
				<body style=\"font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;\">
				  <div style=\"max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);\">
				    <h2 style=\"color: #d32f2f;\">忘記密碼驗證碼</h2>
				    <p>親愛的使用者，您好：</p>
				    <p>以下是您的驗證碼，請於 5 分鐘內輸入以完成驗證：</p>
				    <div style=\"font-size: 24px; font-weight: bold; margin: 20px 0; color: #2e7d32;\">%s</div>
				    <p style=\"margin-top: 30px; font-size: 0.9rem; color: #777;\">體重與健康 AI 追蹤系統 團隊</p>
				  </div>
				</body>
				</html>
				"""
				.formatted(code);
	}

}