package com.example.demo.model.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

// 體重紀錄 DTO：用於新增、查詢、編輯體重紀錄資料
@Data
public class WeightRecordDTO {

	private Integer recordId; // 主鍵 ID，供編輯用

	private Integer accountId; // 帳號 ID，通常由後端從 session 或 JWT 補上

	private Double weight; // 體重（公斤）

	private Double height; // 身高（公分）

	private Integer age; // 年齡（可選欄位，看是否需要使用）

	private Double bmi; // BMI（可由後端計算後帶入）

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate recordDate; // 紀錄日期，格式 yyyy-MM-dd（例如 "2025-06-19"）

}