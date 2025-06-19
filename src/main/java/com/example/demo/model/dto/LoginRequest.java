package com.example.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// 使用者登入請求用 DTO
@Data
public class LoginRequest {

	@NotBlank(message = "帳號不得為空")
	private String username; // 帳號

	@NotBlank(message = "密碼不得為空")
	private String password; // 密碼

	@NotBlank(message = "驗證碼不得為空")
	private String captcha; // 驗證碼（由後端驗證是否正確）
}