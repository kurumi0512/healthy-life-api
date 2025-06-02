package com.example.demo.model.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BloodSugarRecordDTO {
	private Integer recordId; // 紀錄主鍵 ID，用於修改/查詢
	private Integer accountId; // 使用者帳號 ID（必要欄位）
	private Double fasting; // 空腹血糖
	private Double postMeal; // 飯後血糖
	private LocalDate recordDate; // 紀錄日期
	private String notes; // 備註（可為 null）
}