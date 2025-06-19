package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 測試用控制器：用來確認後端是否啟動、API 是否通順
@RestController
@RequestMapping("/health/check")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class TestController {

	// [GET] 基本連線測試：請求 /health/check/ping 時會回傳 "pong"
	@GetMapping("/ping")
	public String ping() {
		return "pong"; // 回傳固定字串，代表伺服器有正常回應
	}
}
