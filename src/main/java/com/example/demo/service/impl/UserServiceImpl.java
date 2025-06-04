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
	private HttpSession session; // 抓取目前登入者帳號資訊

	@Autowired
	private AccountRepository accountRepository; // 操作資料庫用的 Repository

	@Autowired
	private UserRepository userRepository; // 操作資料庫用的 Repository

	// 從 session 中取得目前登入者的 accountId
	@Override
	public User getCurrentLoginUser() {
		Integer accountId = (Integer) session.getAttribute("accountId");
		if (accountId == null)
			return null;

		return userRepository.findByAccount_Id(accountId).orElse(null); // 回傳 null，讓 Controller 判斷
	}

	// 查出所有User,透過 Java Stream 把每筆 User + Account 組成 UserDto，只包含前端需要的欄位。
	// 像是：username, email, status, role（不包含密碼）。
	@Override
	public List<UserDto> findAllUsers() {
		return userRepository.findAll().stream().map(user -> {
			Account account = user.getAccount();
			return new UserDto(user.getId(), account.getUsername(), user.getName(), account.getEmail(),
					account.getStatus(), account.getRole().name() // 只回傳安全的 DTO(不傳密碼..)
			);
		}).collect(Collectors.toList());
	}

	// 用 id 查一筆 User，一樣組成 UserDto 回傳
	// 給「會員資料編輯頁」預設值使用
	@Override
	public UserDto findUserById(Integer id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("查無使用者"));
		Account account = user.getAccount();
		return new UserDto(user.getId(), account.getUsername(), user.getName(), account.getEmail(), account.getStatus(),
				account.getRole().name());
	}

	// 查出 User 並修改姓名
	@Override
	public void updateUser(Integer id, UserDto userDto) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("查無使用者"));
		Account account = user.getAccount();

		user.setName(userDto.getName()); // 修改 User 表的 name 欄位
		account.setEmail(userDto.getEmail()); // 從關聯取得 Account,修改 Account 表的 email 欄位
		account.setStatus(userDto.getStatus()); // 修改 Account 表的狀態
		account.setRole(Role.valueOf(userDto.getRole())); // Role.valueOf是String 轉 Enum

		// 更新了兩個不同的 Entity,所以要放兩次
		// 儲存會員資料編輯結果
		accountRepository.save(account);
		userRepository.save(user);
	}

	// 後台停權使用者帳號
	@Override
	public void deleteUser(Integer id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("查無使用者"));
		Account account = user.getAccount();

		account.setStatus("INACTIVE"); // 用文字代表未啟用狀態
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
