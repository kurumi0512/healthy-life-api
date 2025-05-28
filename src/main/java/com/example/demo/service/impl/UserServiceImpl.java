package com.example.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.Role;
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

		return userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("找不到使用者"));
	}

	@Override
	public List<UserDto> findAllUsers() {
		return userRepository.findAll().stream().map(user -> {
			Account account = user.getAccount();
			return new UserDto(user.getId(), account.getUsername(), user.getName(), account.getEmail(),
					account.getStatus(), account.getRole().name() // ✅ 修正 enum → String
			);
		}).collect(Collectors.toList());
	}

	@Override
	public UserDto findUserById(Integer id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("查無使用者"));
		Account account = user.getAccount();
		return new UserDto(user.getId(), account.getUsername(), user.getName(), account.getEmail(), account.getStatus(),
				account.getRole().name());
	}

	@Override
	public void updateUser(Integer id, UserDto userDto) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("查無使用者"));
		Account account = user.getAccount();

		user.setName(userDto.getName());
		account.setEmail(userDto.getEmail());
		account.setStatus(userDto.getStatus()); // ✅ 使用 status
		account.setRole(Role.valueOf(userDto.getRole())); // ✅ String 轉 Enum

		accountRepository.save(account);
		userRepository.save(user);
	}

	@Override
	public void deleteUser(Integer id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("查無使用者"));
		Account account = user.getAccount();

		account.setStatus("INACTIVE"); // ✅ 用文字代表未啟用狀態
		accountRepository.save(account);
	}

	@Override
	public UserDto findByAccountId(Integer accountId) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("查無使用者"));

		Account account = user.getAccount();
		return new UserDto(user.getId(), account.getUsername(), user.getName(), account.getEmail(), account.getStatus(),
				account.getRole().name());
	}
}
