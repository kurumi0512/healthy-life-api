package com.example.demo.model.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class WeightRecordDTO {
	private Integer recordId;
	private Integer accountId; // ← 前端不一定要送這欄，後端從 session 補上
	private Double weight;
	private Double height;
	private Integer age;
	private Double bmi;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate recordDate;

}