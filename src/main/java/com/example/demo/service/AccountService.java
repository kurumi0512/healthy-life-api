package com.example.demo.service;

import com.example.demo.model.entity.Account;

public interface AccountService {
	void register(String username, String password, String email);

	boolean isUsernameTaken(String username); // 帳號是否存在

	void activateAccount(String username);

	Account findById(Integer id);

	// 傳送驗證碼（TOTP）
	void sendResetCode(String email);

	// 驗證使用者輸入的驗證碼
	boolean verifyResetCode(String email, String code);

	// 重設密碼
	void resetPassword(String email, String newPassword);
}
