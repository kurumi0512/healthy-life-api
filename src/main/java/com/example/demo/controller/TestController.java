package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health/check")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class TestController {

	@GetMapping("/ping")
	public String ping() {
		return "pong"; // 每次請求這個網址時，會回傳字串 "pong"。
	}
}

//🧪 確認後端是否啟動成功	請求 /health/ping 是否回應 "pong"
//🔁 測試前端與後端 CORS 設定是否正常	前端是否能成功 fetch 到這個 API
//🧭 檢查 API 路徑是否有誤	幫助你驗證後端控制器有正常接收請求
//🧍 不需登入即可測試	無需 session 或 token 驗證，快速確認連線性