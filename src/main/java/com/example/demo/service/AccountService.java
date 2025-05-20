package com.example.demo.service;

public interface AccountService {
	void register(String username, String password, String email);

	boolean isUsernameTaken(String username);

	void activateAccount(String username);
}
