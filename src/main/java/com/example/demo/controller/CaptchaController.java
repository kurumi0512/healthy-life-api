package com.example.demo.controller;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CaptchaController {

	@GetMapping("/rest/health/captcha")
	public void getCaptcha(HttpServletResponse response, HttpSession session) throws IOException {
		String code = String.valueOf(new Random().nextInt(9000) + 1000);
		session.setAttribute("captcha", code);

		int width = 120, height = 40;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		// 抗鋸齒
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// 背景色
		g.setColor(new Color(255, 255, 240));
		g.fillRect(0, 0, width, height);

		// 雜訊點（小圓點）
		Random rand = new Random();
		for (int i = 0; i < 30; i++) {
			int x = rand.nextInt(width);
			int y = rand.nextInt(height);
			int r = rand.nextInt(256);
			int gColor = rand.nextInt(256);
			int b = rand.nextInt(256);
			g.setColor(new Color(r, gColor, b, 150));
			g.fillRect(x, y, 1, 1);
		}

		// 字體與樣式
		g.setFont(new Font("Arial", Font.BOLD, 30));

		for (int i = 0; i < code.length(); i++) {
			// 隨機顏色
			int r = rand.nextInt(200);
			int gColor = rand.nextInt(200);
			int b = rand.nextInt(200);
			g.setColor(new Color(r, gColor, b));

			// 隨機旋轉角度 ±15 度
			double angle = Math.toRadians(rand.nextInt(30) - 15);
			g.rotate(angle, 25 + i * 20, 25); // 中心點旋轉

			// 畫出字
			g.drawString(String.valueOf(code.charAt(i)), 20 + i * 20, 30);

			// 畫完後轉回原位
			g.rotate(-angle, 25 + i * 20, 25);
		}

		// 淡色干擾線
		g.setStroke(new BasicStroke(1f));
		for (int i = 0; i < 6; i++) {
			int r = rand.nextInt(150) + 100;
			int gColor = rand.nextInt(150) + 100;
			int b = rand.nextInt(150) + 100;
			g.setColor(new Color(r, gColor, b, 100));
			int x1 = rand.nextInt(width);
			int y1 = rand.nextInt(height);
			int x2 = rand.nextInt(width);
			int y2 = rand.nextInt(height);
			g.drawLine(x1, y1, x2, y2);
		}

		g.dispose();

		response.setContentType("image/jpeg");
		ImageIO.write(image, "JPEG", response.getOutputStream());
	}
}