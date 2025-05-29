package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

		UserProfileDto dto = new UserProfileDto();
		dto.setName(user.getName());
		dto.setAge(user.getAge());
		dto.setGender(user.getGender());
		dto.setGoal(user.getGoal());
		dto.setHeight(user.getHeight());
		dto.setTargetWeight(user.getTargetWeight());
		dto.setAgeGroup(user.getAgeGroup());
		dto.setEmail(account.getEmail());
		return dto;
	}

	@Override
	public void updateProfile(Integer accountId, UserProfileDto dto) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("找不到個人資料"));

		user.setName(dto.getName());
		user.setAge(dto.getAge());
		user.setGender(dto.getGender());
		user.setGoal(dto.getGoal());
		user.setHeight(dto.getHeight());
		user.setTargetWeight(dto.getTargetWeight());
		user.setAgeGroup(dto.getAgeGroup());

		userRepository.save(user);
	}
}
