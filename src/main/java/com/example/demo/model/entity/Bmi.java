package com.example.demo.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 封裝身高、體重與 BMI 的資料結構
// 通常用來回傳計算後的結果，或放入 List 中顯示多筆資料

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bmi {
	private Double height; // 身高（公分）
	private Double weight; // 體重（公斤）
	private Double bmi; // 計算後的 BMI 值

	public static void main(String[] args) {
		// 測試用 main 方法，可用於本地執行簡易測試
	}

}