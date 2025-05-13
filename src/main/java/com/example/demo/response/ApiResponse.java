package com.example.demo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//因為Data不知道是什麼型態,所以給T泛型
//建立 Server 與 Client 在傳遞資料上的統一結構與標準(含錯誤)
//原先有寫一個private Integer status 狀態:400,200,不過可以交由前端處理

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
