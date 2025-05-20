package com.example.demo.model.dto;

import lombok.Data;

@Data
public class LoginRequest {
	private String username;
	private String password;
	private String captcha; // 後端驗證碼欄位
}