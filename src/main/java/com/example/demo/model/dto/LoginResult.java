package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResult {

	private boolean success; // 是否登入成功
	private String message; // 回應訊息（成功/錯誤提示）
	private Integer id; // 使用者 ID
	private String username; // 帳號
	private String email; // 信箱
	private String name; // 使用者名稱（顯示用）

}
