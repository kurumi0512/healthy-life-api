package com.example.demo.model.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

// DTO：血糖紀錄用資料物件，供前後端傳遞使用
@Data
public class BloodSugarRecordDTO {

	private Integer recordId; // 主鍵 ID，供編輯或更新使用

	private Integer accountId; // 帳號 ID，後端根據登入使用者自動設定，不由前端傳入

	private Double fasting; // 餐前血糖值（單位：mg/dL 或 mmol/L，視專案需求）

	private Double postMeal; // 餐後血糖值

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate recordDate; // 紀錄日期，格式 yyyy-MM-dd，前端應傳字串如 "2025-06-19"

	private String notes; // 備註（可為 null，用於補充說明）
}