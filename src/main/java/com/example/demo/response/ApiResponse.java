package com.example.demo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 *  ApiResponse<T> 是一個自訂的泛型回應格式類別，用來統一後端回傳給前端的資料結構
 * 
 * - T 為泛型，表示不限定資料型別（例如 List<User>、Map<String,Object>、單筆物件皆可）
 * - 此類別可用於成功或錯誤回應時的資料封裝
 * - 目的是讓前後端溝通時的格式一致，便於處理與顯示
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

	private String message; // 回應訊息，例如 "查詢成功"、"新增成功"、"計算失敗" 等

	private T data; // 真正要回傳的資料內容，可為物件、清單、null 等

	// 成功回應工廠方法
	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(message, data);
	}

	// 失敗回應工廠方法（無 data，只帶錯誤訊息）
	public static <T> ApiResponse<T> error(String message) {
		return new ApiResponse<>(message, null);
	}
}