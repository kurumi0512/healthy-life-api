package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.Role;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AccountService;
import com.example.demo.util.HashUtil;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public void register(String username, String password, String email) {
		String salt = HashUtil.generateSalt();
		String hashPassword = HashUtil.hashPassword(password, salt);

		Account account = new Account();
		account.setUsername(username);
		account.setEmail(email);
		account.setHashSalt(salt);
		account.setHashPassword(hashPassword);
		account.setCompleted(false); // 尚未驗證
		account.setStatus("UNVERIFIED");
		account.setRole(Role.USER); // 預設使用者角色
		account.setCreateTime(LocalDateTime.now());

		accountRepository.save(account); // ✅ 儲存帳號

		// ✅ 建立對應的 User，name 預設為 username
		User user = new User();
		user.setAccount(account);
		user.setCreateTime(LocalDateTime.now());
		user.setName(username); // ✅ 將帳號當作預設姓名
		userRepository.save(user); // ✅ 儲存 user
	}

	@Override
	public boolean isUsernameTaken(String username) {
		return accountRepository.findByUsername(username).isPresent();
	}

	@Override
	public void activateAccount(String username) {
		Optional<Account> optional = accountRepository.findByUsername(username);
		if (optional.isPresent()) {
			Account account = optional.get();
			account.setCompleted(true); // ✅ 標記為已完成驗證
			account.setStatus("ACTIVE"); // ✅ 狀態改為有效
			accountRepository.save(account);
		} else {
			throw new RuntimeException("查無帳號：" + username);
		}
	}
}