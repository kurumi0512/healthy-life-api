package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.AccountRepository;
import com.example.demo.util.HashUtil;

@Service
public class AuthService {

	@Autowired
	private AccountRepository accountRepository;

	public boolean validate(String username, String rawPassword) {
		return accountRepository.findByUsername(username).map(account -> {
			// 取出使用者的鹽與雜湊密碼
			String dbSalt = account.getHashSalt();
			String dbHashPassword = account.getHashPassword();

			// 將使用者輸入的密碼 + salt 做 hash
			String inputHash = HashUtil.hashPassword(rawPassword, dbSalt);

			// 比對結果
			return inputHash.equals(dbHashPassword);
		}).orElse(false);
	}
}
