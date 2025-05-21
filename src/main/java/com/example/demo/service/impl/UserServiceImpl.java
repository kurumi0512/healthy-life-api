package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		Integer accountId = (Integer) session.getAttribute("accountId");

		if (accountId == null) {
			throw new RuntimeException("尚未登入");
		}

		return userRepository.findByAccountId(accountId).orElseThrow(() -> new RuntimeException("找不到使用者"));
	}
}
