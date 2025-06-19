package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 簡化版使用者資料傳輸物件（DTO）
// 用於回傳登入狀態、基本帳號資訊等場景
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSimpleDto {
	private Integer accountId; // Account 表的主鍵 ID
	private String username; // 使用者帳號名稱
}
