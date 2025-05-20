package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.LoginResult;
import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.HashUtil;

@Service
public class AuthService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UserRepository userRepository;

	public LoginResult validate(String username, String rawPassword) {
		Optional<Account> optAccount = accountRepository.findByUsername(username);

		if (optAccount.isEmpty()) {
			return new LoginResult(false, "帳號不存在", null, null, null);
		}

		Account account = optAccount.get();
		String inputHash = HashUtil.hashPassword(rawPassword, account.getHashSalt());

		if (!inputHash.equals(account.getHashPassword())) {
			return new LoginResult(false, "密碼錯誤", null, null, null);
		}

		// ✅ 根據帳號查使用者
		User user = userRepository.findByAccount(account);

		// ✅ 回傳包含使用者姓名
		LoginResult result = new LoginResult(true, "登入成功", account.getId(), account.getUsername(), account.getEmail());

		return result;
	}
}