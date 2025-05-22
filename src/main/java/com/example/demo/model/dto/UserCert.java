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
	private String role; // "USER" / "ADMIN"
	private String name; // 顯示用，來自 User 表
	private String email; // 來自 Account 表
	private boolean verified; // Account.completed，是否已驗證
	private boolean admin; // role.equals("ADMIN")
	private boolean userCompleted;
}
