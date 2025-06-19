package com.example.demo.service;

import com.example.demo.model.entity.Account;

//帳號管理的服務層介面（註冊、啟用、重設密碼、驗證帳號等功能）
public interface AccountService {

	// 使用者註冊
	void register(String username, String password, String email);

	// 檢查帳號是否已存在
	boolean isUsernameTaken(String username);

	// 啟用帳號
	void activateAccount(String username);

	// 根據 ID 查詢帳號資訊
	Account findById(Integer id);

	// 傳送密碼重設驗證碼
	void sendResetCode(String email);

	// 驗證使用者輸入的驗證碼是否正確
	boolean verifyResetCode(String email, String code);

	// 重設密碼
	void resetPassword(String email, String newPassword);
}
