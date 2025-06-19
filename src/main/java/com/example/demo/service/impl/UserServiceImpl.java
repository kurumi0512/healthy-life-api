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
	private HttpSession session; // 取得目前登入中的使用者資訊

	@Autowired
	private AccountRepository accountRepository; // 操作帳號資料表

	@Autowired
	private UserRepository userRepository; // 操作使用者資料表

	// 取得目前登入者對應的 User 實體（從 session 取得 accountId）
	@Override
	public User getCurrentLoginUser() {
		Integer accountId = (Integer) session.getAttribute("accountId");
		if (accountId == null)
			return null;

		return userRepository.findByAccount_Id(accountId).orElse(null);
	}

	// 查詢所有使用者資料，並轉換為 UserDto 列表（不回傳敏感欄位如密碼）
	@Override
	public List<UserDto> findAllUsers() {
		return userRepository.findAll().stream().map(user -> {
			Account account = user.getAccount();
			return new UserDto(user.getId(), account.getUsername(), user.getName(), account.getEmail(),
					account.getStatus(), account.getRole().name());
		}).collect(Collectors.toList());
	}

	// 根據使用者 ID 查詢資料，並轉換為 UserDto（用於編輯個人資料）
	@Override
	public UserDto findUserById(Integer id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("查無使用者"));
		Account account = user.getAccount();
		return new UserDto(user.getId(), account.getUsername(), user.getName(), account.getEmail(), account.getStatus(),
				account.getRole().name());
	}

	// 更新使用者資料（含姓名、Email、狀態與角色）
	@Override
	public void updateUser(Integer id, UserDto userDto) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("查無使用者"));
		Account account = user.getAccount();

		user.setName(userDto.getName()); // 修改 User 表的姓名
		account.setEmail(userDto.getEmail()); // 修改帳號 Email
		account.setStatus(userDto.getStatus()); // 修改帳號狀態
		account.setRole(Role.valueOf(userDto.getRole())); // 字串轉換為 Enum Role

		// 分別儲存兩個 Entity（account 與 user）
		accountRepository.save(account);
		userRepository.save(user);
	}

	// 停權使用者帳號（設為 INACTIVE）
	@Override
	public void deleteUser(Integer id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("查無使用者"));
		Account account = user.getAccount();

		account.setStatus("INACTIVE"); // 標記為停權狀態
		accountRepository.save(account);
	}

	// 根據帳號 ID 查詢對應的使用者資料（轉為 DTO 回傳）
	@Override
	public UserDto findByAccountId(Integer accountId) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("查無使用者"));
		Account account = user.getAccount();
		return new UserDto(user.getId(), account.getUsername(), user.getName(), account.getEmail(), account.getStatus(),
				account.getRole().name());
	}
}
