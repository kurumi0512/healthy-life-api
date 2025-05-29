package com.example.demo.service;

import com.example.demo.model.entity.Account;

public interface AccountService {
	void register(String username, String password, String email);

	boolean isUsernameTaken(String username); // 帳號是否存在

	void activateAccount(String username);

	Account findById(Integer id);
}
