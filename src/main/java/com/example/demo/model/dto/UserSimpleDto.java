package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

//簡化版使用者資料傳輸物件（DTO）
public class UserSimpleDto {
	private Integer accountId;
	private String username;
}
