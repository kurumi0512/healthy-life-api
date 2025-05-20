package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResult {
	private boolean success;
	private String message;
	private Integer id;
	private String username;
	private String email;
}
