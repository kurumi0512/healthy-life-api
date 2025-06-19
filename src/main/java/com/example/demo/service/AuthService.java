package com.example.demo.service;

import com.example.demo.exception.CertException;
import com.example.demo.model.dto.UserCert;

//認證服務介面（登入驗證專用）
public interface AuthService {

	// 驗證使用者帳號與原始密碼，成功則回傳認證資訊 UserCert，失敗會拋出例外
	UserCert validate(String username, String rawPassword) throws CertException;
}
