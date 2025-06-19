package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 用來回傳使用者是否已登入的狀態 + 使用者簡易資料
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginCheckResponse {
	private boolean loggedIn; // 是否已登入（true / false）
	private UserSimpleDto user; // 使用者簡要資訊（若未登入可為 null）
}
