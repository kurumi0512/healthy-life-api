package com.example.demo.model.dto;

import java.time.LocalDateTime;

import com.example.demo.model.enums.Role;

import lombok.Data;

// DTO：後台帳號管理用（純 Account 資訊）
// 只回傳需要的欄位，避免洩漏敏感資料（如密碼）
@Data
public class AccountResponseDTO {

	private Integer id; // 帳號編號
	private String username; // 使用者名稱
	private String email; // 信箱
	private Boolean completed; // 是否完成設定（如有沒有填完個人資料）
	private LocalDateTime createTime; // 建立時間
	private LocalDateTime lastLogin; // 最後登入時間
	private String status; // 狀態（如：啟用中/停用中）
	private Role role; // 角色（如：ADMIN / USER）
}
