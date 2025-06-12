package com.example.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

	@NotBlank(message = "帳號不得為空")
	private String username;

	@NotBlank(message = "密碼不得為空")
	private String password;

	@NotBlank(message = "驗證碼不得為空")
	private String captcha; // 後端驗證碼欄位
}