package com.example.demo.model.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

// DTO：用於血壓紀錄的資料傳輸物件
@Data
public class BloodPressureRecordDTO {

	private Integer recordId; // 主鍵 ID（更新用）

	@Min(value = 50, message = "收縮壓不能小於 50")
	@Max(value = 250, message = "收縮壓不能超過 250")
	private Integer systolic; // 收縮壓（上壓）

	@Min(value = 50, message = "舒張壓不能小於 50")
	@Max(value = 250, message = "舒張壓不能超過 250")
	private Integer diastolic; // 舒張壓（下壓）

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate recordDate; // 測量日期（格式 yyyy-MM-dd）

	@Column(nullable = true)
	private String notes; // 備註，可為空

	private Integer accountId; // 帳號 ID（從登入使用者補上，不由前端直接傳）
}