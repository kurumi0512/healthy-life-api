package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//前台個人頁面（個人檔案）
//回傳綜合使用者資訊	
//整合 User 表 + Account 表

public class UserDto {
	private Integer id; // User 表的主鍵
	private String username; // 帳號名稱（來自 Account 表）
	private String name; // 使用者姓名
	private String email; // 信箱（來自 Account 表）
	private String status; // 是否啟用(啟用會變成active)
	private String role; // 角色（admin、user）

}
