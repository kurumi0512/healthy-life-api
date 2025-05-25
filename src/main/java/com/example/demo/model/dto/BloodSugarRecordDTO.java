package com.example.demo.model.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BloodSugarRecordDTO {
	private Integer id; // 可選：查詢時顯示用
	private Double fasting; // 空腹血糖
	private Double postMeal; // 飯後血糖
	private LocalDate recordDate; // 記錄日期
	private String notes; // 備註（可為 null）

}
