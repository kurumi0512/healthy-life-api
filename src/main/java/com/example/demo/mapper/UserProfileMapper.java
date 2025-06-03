package com.example.demo.mapper;

import java.time.LocalDate;
import java.time.Period;

import com.example.demo.model.dto.UserProfileDto;
import com.example.demo.model.entity.User;

public class UserProfileMapper {

	public static UserProfileDto toDto(User user, String email) {
		if (user == null)
			return null;

		UserProfileDto dto = new UserProfileDto();
		dto.setId(user.getId());
		dto.setName(user.getName());
		dto.setBirthDate(user.getBirthDate() != null ? user.getBirthDate().toString() : null);
		dto.setGender(user.getGender());
		dto.setGoal(user.getGoal());
		dto.setHeight(user.getHeight());
		dto.setTargetWeight(user.getTargetWeight());
		dto.setAgeGroup(getAgeGroup(user.getBirthDate())); // ✅ 直接用出生年月計算
		dto.setEmail(email);
		return dto;
	}

	public static void updateEntity(User user, UserProfileDto dto) {
		if (user == null || dto == null)
			return;

		user.setName(dto.getName());
		user.setBirthDate(dto.getBirthDate() != null ? LocalDate.parse(dto.getBirthDate()) : null);
		user.setGender(dto.getGender());
		user.setGoal(dto.getGoal());
		user.setHeight(dto.getHeight());
		user.setTargetWeight(dto.getTargetWeight());
		user.setAgeGroup(getAgeGroup(user.getBirthDate())); // ✅ 同樣重新設定
	}

	private static String getAgeGroup(LocalDate birthDate) {
		if (birthDate == null)
			return "";

		int age = Period.between(birthDate, LocalDate.now()).getYears();
		if (age <= 12)
			return "兒童";
		else if (age <= 19)
			return "青少年";
		else if (age <= 29)
			return "青年";
		else if (age <= 64)
			return "壯年";
		else
			return "老年";
	}
}