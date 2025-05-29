package com.example.demo.model.dto;

import java.time.LocalDateTime;

import com.example.demo.model.enums.Role;

import lombok.Data;

//後臺帳號管理用
//純粹 Account 表
@Data
public class AccountResponseDTO {
	private Integer id;
	private String username;
	private String email;
	private Boolean completed;
	private LocalDateTime createTime;
	private LocalDateTime lastLogin;
	private String status;
	private Role role;
}
