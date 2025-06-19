package com.example.demo.model.dto;

import lombok.Data;

// 前台個人檔案頁用 DTO
// 整合 User 表 + Account 表的資訊
@Data
public class UserProfileDto {
	private Integer id; // User 表主鍵
	private String name; // 使用者姓名
	private String birthDate; // 出生年月日（格式 yyyy-MM-dd，為了方便表單處理，使用 String 類型）
	private String gender; // 性別（如 male、female、other）
	private String goal; // 健康目標（如減重、增肌、維持）
	private Double height; // 身高（公分）
	private Double targetWeight; // 目標體重（公斤）
	private String ageGroup; // 年齡分組（系統根據 birthDate 自動計算：如 青年、壯年等）
	private String email; // 使用者信箱（來自 Account 表）
}