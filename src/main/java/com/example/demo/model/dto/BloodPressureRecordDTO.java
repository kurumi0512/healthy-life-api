package com.example.demo.model.dto;

import lombok.Data;

@Data
public class BloodPressureRecordDTO {
	private Integer recordId;
	private Integer systolic; // 收縮壓
	private Integer diastolic; // 舒張壓
	private String recordDate;
	private String notes; // 備註
	private Integer accountId; // 從 session 裡取得並補上
}
