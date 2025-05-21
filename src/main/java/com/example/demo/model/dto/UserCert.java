package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

// 使用者登入後的憑證資料（安全封裝）
@AllArgsConstructor
@Getter
@ToString
public class UserCert {
	private Integer accountId;
	private String username;
	private String role;
	private String name; // 來自 User 表
	private String email; // 從 Account 表來
}
