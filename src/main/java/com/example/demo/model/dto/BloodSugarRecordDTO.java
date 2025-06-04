package com.example.demo.model.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class BloodSugarRecordDTO {
	private Integer recordId; // 編輯用主鍵 ID
	private Integer accountId; // 後端自動設定，不由前端傳入

	private Double fasting; // 餐前血糖
	private Double postMeal; // 餐後血糖

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate recordDate;

	private String notes; // 備註，可為 null
}