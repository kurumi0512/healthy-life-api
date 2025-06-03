package com.example.demo.model.dto;

import lombok.Data;

@Data
public class UserProfileDto {
	private Integer id;
	private String name;
	private String birthDate;
	private String gender;
	private String goal;
	private Double height;
	private Double targetWeight;
	private String ageGroup;
	private String email; // 從 Account 表來，方便顯示
}