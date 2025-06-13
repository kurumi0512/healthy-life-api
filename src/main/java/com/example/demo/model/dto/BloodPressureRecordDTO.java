package com.example.demo.model.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BloodPressureRecordDTO {

	private Integer recordId;

	@Min(value = 50, message = "收縮壓不能小於 50")
	@Max(value = 250, message = "收縮壓不能超過 250")
	private Integer systolic;

	@Min(value = 50, message = "舒張壓不能小於 50")
	@Max(value = 250, message = "舒張壓不能超過 250")
	private Integer diastolic;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate recordDate;

	@Size(max = 50, message = "備註最多 50 字")
	private String notes;

	private Integer accountId; // 從 session 裡補上
}