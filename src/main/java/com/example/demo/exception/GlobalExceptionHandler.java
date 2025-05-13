package com.example.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.response.ApiResponse;

// 利用 @ControllerAdvice 的特性來處理全局錯誤
@ControllerAdvice
public class GlobalExceptionHandler {

	// 當系統發生例外錯誤,寫Exception.class就所有Exception子類別發生的錯誤,都會來這邊處理
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
		// MethodArgumentTypeMismatchException,特別抓起來修改
		// NoResourceFoundException,特別抓出來修改
		String errorMessage = e.toString();
		// e.getClass() 這是 Java 所有物件繼承自 Object 類別的方法，會傳回該物件的 Class 類別資訊。
		// .getSimpleName()是 Class 類別中的方法，會傳回該類別的「簡單名稱」（不含 package 路徑）。

		switch (e.getClass().getSimpleName()) {
		case "MethodArgumentTypeMismatchException":
			errorMessage = "參數錯誤(" + e.getClass().getSimpleName() + ")";
			break;
		case "NoResourceFoundException":
			errorMessage = "查無網頁(" + e.getClass().getSimpleName() + ")";
			break;
		}
		ApiResponse<Object> apiResponse = ApiResponse.error(errorMessage);
		return ResponseEntity.badRequest().body(apiResponse);
	}
}