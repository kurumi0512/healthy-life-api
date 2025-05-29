package com.example.demo.service;

import com.example.demo.model.dto.UserProfileDto;

public interface UserProfileService {
	UserProfileDto getProfile(Integer accountId);

	void updateProfile(Integer accountId, UserProfileDto dto);
}
