package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.entity.Account;
import com.example.demo.repository.AccountRepository;
import com.example.demo.util.HashUtil;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	public void activateAccount(String username) {
		accountRepository.findByUsername(username).ifPresent(account -> {
			account.setStatus("active");
			accountRepository.save(account);
		});
	}

	public boolean isUsernameTaken(String username) {
		return accountRepository.findByUsername(username).isPresent();
	}

	@Override
	public void register(String username, String password, String email) {
		String salt = HashUtil.generateSalt();
		String hashPassword = HashUtil.hashPassword(password, salt);

		Account account = new Account();
		account.setUsername(username);
		account.setEmail(email);
		account.setHashSalt(salt);
		account.setHashPassword(hashPassword);
		account.setCompleted(false);
		account.setStatus("active");
		account.setCreateTime(LocalDateTime.now());

		accountRepository.save(account);
	}

}