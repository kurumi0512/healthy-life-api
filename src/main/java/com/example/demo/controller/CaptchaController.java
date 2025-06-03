package com.example.demo.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

//圖形驗證碼產生器
@RestController // REST 控制器
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") // 跨域請求
public class CaptchaController {

	@GetMapping("/rest/health/captcha")
	public void getCaptcha(HttpServletResponse response, HttpSession session) throws IOException {
		// 產生四位數驗證碼（1000~9999）
		String code = String.valueOf(new Random().nextInt(9000) + 1000);
		session.setAttribute("captcha", code); // 儲存到 Session

		// 建立圖片物件與畫布
		int width = 100, height = 40;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics(); // 取得畫筆G來畫東西

		// 背景與字體樣式
		g.setColor(Color.WHITE); // 白色背景
		g.fillRect(0, 0, width, height); // 設定黑色字體,粗體字
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 28));
		g.drawString(code, 22, 30); // X 與 Y 座標要對齊

		// 加入干擾線條
		g.setColor(Color.RED);
		Random rand = new Random();
		for (int i = 0; i < 25; i++) { // 畫 25 條紅色干擾線，增加辨識困難度，防止機器辨識圖片內容
			int x1 = rand.nextInt(width);
			int y1 = rand.nextInt(height);
			int x2 = rand.nextInt(width);
			int y2 = rand.nextInt(height);
			g.drawLine(x1, y1, x2, y2);
		}

		// 回傳 JPEG 圖片
		response.setContentType("image/jpeg");
		ImageIO.write(image, "JPEG", response.getOutputStream());
	}
}