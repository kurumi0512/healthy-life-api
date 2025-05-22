package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	private Integer Id; // User 表的主鍵
	private String username; // 帳號名稱（來自 Account 表）
	private String name; // 使用者姓名
	private String email; // 信箱（來自 Account 表）
	private String status; // 是否啟用
	private String role; // 角色（admin、user）

}
