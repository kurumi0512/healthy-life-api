package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.CertException;
import com.example.demo.model.dto.UserCert;
import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.util.HashUtil;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private AccountRepository accountRepository; // 操作帳號資料表（帳號、密碼、登入資訊）

	@Autowired
	private UserRepository userRepository; // 操作使用者基本資料表（姓名、生日等）

	/**
	 * ✅ 登入流程核心邏輯 根據帳號與密碼驗證身份，成功後回傳 UserCert（登入憑證）
	 */
	@Override
	public UserCert validate(String username, String rawPassword) throws CertException {
		// 1️. 查詢帳號是否存在
		Optional<Account> optAccount = accountRepository.findByUsername(username);
		if (optAccount.isEmpty()) {
			throw new CertException("帳號不存在");
		}

		Account account = optAccount.get();

		// 2️. 將使用者輸入的明文密碼加鹽後雜湊，比對是否正確
		String inputHash = HashUtil.hashPassword(rawPassword, account.getHashSalt());
		if (!inputHash.equals(account.getHashPassword())) {
			throw new CertException("密碼錯誤");
		}

		// 3️. 登入成功 → 更新帳號的最後登入時間
		account.setLastLogin(LocalDateTime.now());
		accountRepository.save(account);

		// 4️. 取得對應的 User 資料（可能為 null，代表尚未補完個人資料）
		User user = userRepository.findByAccount_Id(account.getId()).orElse(null);

		// 5️. 封裝成 UserCert，回傳前端
		return toUserCert(account, user);
	}

	/**
	 * 將帳號與使用者資料封裝為 UserCert DTO（登入憑證）
	 */
	private UserCert toUserCert(Account account, User user) {
		// 判斷使用者是否已填完必要欄位（生日與性別）
		boolean userCompleted = user != null && user.getBirthDate() != null && user.getGender() != null;

		// 組合回傳的憑證欄位內容
		return new UserCert(account.getId(), // 帳號 ID
				account.getUsername(), // 使用者名稱
				account.getRole().name(), // 角色名稱（USER / ADMIN）
				user != null ? user.getName() : null, // 姓名
				account.getEmail(), // Email
				account.getCompleted(), // 是否已驗證 email
				"ADMIN".equals(account.getRole().name()), // 是否為管理員
				userCompleted // 是否已補完個人資料
		);
	}
}