package com.example.demo.model.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class BloodPressureRecordDTO {
	private Integer recordId;
	private Integer systolic; // 收縮壓
	private Integer diastolic; // 舒張壓
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate recordDate;
	private String notes; // 備註
	private Integer accountId; // 從 session 裡取得並補上
}
