package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private HttpSession session;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public User getCurrentLoginUser() {
		// 從 session 中取得 accountId（你在登入時應該有放進去）
		Integer accountId = (Integer) session.getAttribute("accountId");

		if (accountId == null) {
			throw new RuntimeException("尚未登入");
		}

		Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("找不到帳號"));

		return userRepository.findByAccount(account);
	}
}
