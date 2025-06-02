package com.example.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.response.ApiResponse;

//Spring Boot 全域例外處理機制,統一處理控制器中可能發生的例外錯誤，避免每個 Controller 都重複寫 try-catch

@ControllerAdvice // 全域異常處理註解
public class GlobalExceptionHandler {

	// 當系統發生例外錯誤,寫Exception.class就所有Exception子類別發生的錯誤,都會來這邊處理

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
		String errorMessage = e.toString();

		// 依據例外的「簡單類別名稱」判斷錯誤類型。
		// 若是請求參數錯誤、查無頁面等情境，就給使用者更明確的訊息。
		switch (e.getClass().getSimpleName()) {
		case "MethodArgumentTypeMismatchException":
			errorMessage = "參數錯誤(" + e.getClass().getSimpleName() + ")";
			break; // e.getClass() 這是 Java 所有物件繼承自 Object
		// 類別的方法，會傳回該物件的 Class 類別資訊。
		case "NoResourceFoundException":
			errorMessage = "查無網頁(" + e.getClass().getSimpleName() + ")";
			break;
		}
		// .getSimpleName()是 Class
		// 類別中的方法，會傳回該類別的「簡單名稱」（不含 package 路徑）。

		ApiResponse<Object> apiResponse = ApiResponse.error(errorMessage); // 你自訂的回應格式類別（通常有欄位 like success, message,
																			// data）
		return ResponseEntity.badRequest().body(apiResponse);
		// HTTP 狀態碼為 400 Bad Request。
		// 回傳統一格式的 JSON 錯誤回應給前端。
	}
}