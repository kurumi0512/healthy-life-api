package com.example.demo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// API(應用程式介面)，是一種「讓不同系統之間可以互相溝通的方法和規則」
//自訂的泛型回應格式類別，用來統一後端回應給前端的格式
//因為Data不知道是什麼型態,所以用泛型T
//建立 Server 與 Client 在傳遞資料上的統一結構與標準(含錯誤)

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
	private String message; // 訊息 例如: 查詢成功, 新增成功, 計算成功...
	T data; // payload 實際資料

	// 成功回應,會回傳ApiResponse的泛型
	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<T>(message, data);
	}

	// 失敗回應
	public static <T> ApiResponse<T> error(String message) {
		return new ApiResponse<T>(message, null);
	}
}
