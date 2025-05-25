package com.example.demo.model.dto;

import lombok.Data;

@Data
public class BloodPressureRecordDTO {
	private Integer recordId;
	private Integer systolic;
	private Integer diastolic;
	private String recordDate;
	private String notes;
	private Integer accountId; // 從 session 裡取得並補上
}
