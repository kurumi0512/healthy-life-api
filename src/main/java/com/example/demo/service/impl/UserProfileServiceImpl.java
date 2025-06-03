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

	/**
	 * 1.查詢 User 資料：透過 userRepository.findByAccount_Id(accountId) 查找與這個帳號綁定的使用者資料。
	 * 2.查詢 Account 資料：取得這個帳號的基本資訊（特別是 email）。 3.封裝成
	 * DTO：呼叫UserProfileMapper.toDto(...) 將 User + email 整合轉換成 UserProfileDto 傳給前端。
	 * 
	 */

	@Override
	public UserProfileDto getProfile(Integer accountId) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("找不到個人資料"));
		Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("找不到帳號資料"));

		return UserProfileMapper.toDto(user, account.getEmail()); // UserProfileMapper轉換,封裝成DTO
	}

//	查出目前登入者的 User 資料（根據 Account ID）
//
//	利用 UserProfileMapper 將 DTO 中的資料更新到 User Entity
//
//	儲存更新後的 User 資料

	@Override
	public void updateProfile(Integer accountId, UserProfileDto dto) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("找不到個人資料"));

		UserProfileMapper.updateEntity(user, dto); // UserProfileMapper轉換
		userRepository.save(user);
	}
}
