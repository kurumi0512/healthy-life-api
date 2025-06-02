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
	private UserRepository userRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public UserProfileDto getProfile(Integer accountId) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("找不到個人資料"));
		Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("找不到帳號資料"));

		return UserProfileMapper.toDto(user, account.getEmail()); // UserProfileMapper轉換
	}

	@Override
	public void updateProfile(Integer accountId, UserProfileDto dto) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("找不到個人資料"));

		UserProfileMapper.updateEntity(user, dto); //// UserProfileMapper轉換
		userRepository.save(user);
	}
}
