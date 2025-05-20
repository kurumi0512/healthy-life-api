package com.example.demo.service;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	private final String googleAppPassword = "ydgw dfai heiz qxqa";
	private final String from = "foreverlove0512t@gmail.com";

	public void sendEmail(String to, String confirmUrl) {
		String host = "smtp.gmail.com";

		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");

		Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, googleAppPassword);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject("健康管理系統會員註冊確認信");
			message.setText("請點選以下連結進行確認：\n" + confirmUrl);

			Transport.send(message);
			System.out.println("✅ 發送成功：" + to);

		} catch (MessagingException e) {
			System.out.println("❌ 發送失敗：" + e.getMessage());
		}
	}
}
