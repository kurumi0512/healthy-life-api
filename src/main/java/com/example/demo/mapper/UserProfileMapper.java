package com.example.demo.mapper;

import com.example.demo.model.dto.UserProfileDto;
import com.example.demo.model.entity.User;

public class UserProfileMapper {

	// Entity ➡ DTO（回傳給前端用）
	public static UserProfileDto toDto(User user, String email) {
		if (user == null)
			return null;

		UserProfileDto dto = new UserProfileDto();
		dto.setName(user.getName());
		dto.setAge(user.getAge());
		dto.setGender(user.getGender());
		dto.setGoal(user.getGoal());
		dto.setHeight(user.getHeight());
		dto.setTargetWeight(user.getTargetWeight());
		dto.setAgeGroup(user.getAgeGroup());
		dto.setEmail(email); // 額外補上來自 Account 的 email
		return dto;
	}

	// DTO ➡ 更新 Entity（前端送資料進來修改用）
	public static void updateEntity(User user, UserProfileDto dto) {
		if (user == null || dto == null)
			return;

		user.setName(dto.getName());
		user.setAge(dto.getAge());
		user.setGender(dto.getGender());
		user.setGoal(dto.getGoal());
		user.setHeight(dto.getHeight());
		user.setTargetWeight(dto.getTargetWeight());
		user.setAgeGroup(dto.getAgeGroup());
	}
}