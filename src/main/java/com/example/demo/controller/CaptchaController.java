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

// 驗證碼圖片產生器
@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CaptchaController {

	// [GET] 產生驗證碼圖片（會存入 session，供前端驗證用）
	@GetMapping("/rest/health/captcha")
	public void getCaptcha(HttpServletResponse response, HttpSession session) throws IOException {
		// 1. 產生 4 位數隨機驗證碼並存入 session
		String code = String.valueOf(new Random().nextInt(9000) + 1000);
		session.setAttribute("captcha", code); // 存入 session，供前端驗證用

		// 2. 建立圖片畫布（寬120px、高40px）
		int width = 120, height = 40;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		// 開啟抗鋸齒（讓字體更平滑）
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// 3. 設定背景色（淡黃色）
		g.setColor(new Color(255, 255, 240));
		g.fillRect(0, 0, width, height);

		// 4. 加入雜訊點（30個隨機色點，增加防機器識別）
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

		// 5. 設定字體（Arial、粗體、30pt）
		g.setFont(new Font("Arial", Font.BOLD, 30));

		// 6. 畫出每個字元（每個字都有不同顏色與旋轉角度）
		for (int i = 0; i < code.length(); i++) {
			// 隨機顏色
			int r = rand.nextInt(200);
			int gColor = rand.nextInt(200);
			int b = rand.nextInt(200);
			g.setColor(new Color(r, gColor, b));

			// 設定隨機旋轉角度 ±15 度
			double angle = Math.toRadians(rand.nextInt(30) - 15);
			g.rotate(angle, 25 + i * 20, 25); // 中心點旋轉

			// 畫出字元
			g.drawString(String.valueOf(code.charAt(i)), 20 + i * 20, 30);

			// 畫完後轉回原位
			g.rotate(-angle, 25 + i * 20, 25);
		}

		// 7. 加入干擾線（6條半透明淡色線條）
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

		g.dispose(); // 釋放圖形物件資源

		// 8. 將圖片輸出為 JPEG 並回傳給前端
		response.setContentType("image/jpeg");
		ImageIO.write(image, "JPEG", response.getOutputStream());
	}
}