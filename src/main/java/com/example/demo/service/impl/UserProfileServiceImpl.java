package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.UserProfileMapper;
import com.example.demo.model.dto.UserProfileDto;
import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserProfileService;

@Service
public class UserProfileServiceImpl implements UserProfileService {

	@Autowired
	private UserRepository userRepository; // 操作 User 資料表

	@Autowired
	private AccountRepository accountRepository; // 操作 Account 資料表

	// 取得個人資料（包含 email）
	@Override
	public UserProfileDto getProfile(Integer accountId) {
		// 查找對應帳號的使用者資料
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("找不到個人資料"));

		// 查找帳號資料（主要是 email）
		Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("找不到帳號資料"));

		// 將 User + email 組合成 UserProfileDto 傳給前端
		return UserProfileMapper.toDto(user, account.getEmail());
	}

	// 更新個人資料
	@Override
	public void updateProfile(Integer accountId, UserProfileDto dto) {
		// 查出使用者資料
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("找不到個人資料"));

		// 將前端送來的 DTO 內容更新進 user entity
		UserProfileMapper.updateEntity(user, dto);

		// 儲存更新後的 user 資料
		userRepository.save(user);
	}
}