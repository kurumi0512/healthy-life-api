package com.example.demo.model.dto;

import lombok.Data;

@Data
public class LoginResult {
	private boolean success;
	private String message;
	private Integer id;
	private String username;
	private String email;
	private String name; // ✅ 加上使用者名稱

	public LoginResult(boolean success, String message, Integer id, String username, String email) {
		this.success = success;
		this.message = message;
		this.id = id;
		this.username = username;
		this.email = email;
	}
}
