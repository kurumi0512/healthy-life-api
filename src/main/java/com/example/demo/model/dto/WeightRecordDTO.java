package com.example.demo.model.dto;

import lombok.Data;

@Data
public class WeightRecordDTO {
	private Integer recordId;
	private Integer accountId; // ← 前端不一定要送這欄，後端從 session 補上
	private Double weight;
	private Double height;
	private Integer age;
	private Double bmi;
	private String recordDate;

}