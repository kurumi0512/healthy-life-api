package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 前台個人頁面專用 DTO
// 整合 User 表（個人資料）+ Account 表（帳號資訊）
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserDto {
	private Integer id; // User 表的主鍵
	private String username; // 帳號名稱（來自 Account 表）
	private String name; // 使用者姓名
	private String email; // 信箱（來自 Account 表）
	private String status; // 是否啟用(啟用會變成active)
	private String role; // 角色（admin、user）

}
