package com.example.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

//Spring Boot 全域例外處理機制,統一處理控制器中可能發生的例外錯誤，避免每個 Controller 都重複寫 try-catch

@ControllerAdvice // 全域異常處理註解
public class GlobalExceptionHandler {

	// 當系統發生例外錯誤,寫Exception.class就所有Exception子類別發生的錯誤,都會來這邊處理

	@ExceptionHandler(Exception.class)
	public Object handleException(HttpServletRequest request, Exception ex) {
		String acceptHeader = request.getHeader("Accept");

		// 若是 SSE 請求，不回傳 ApiResponse，讓 emitter.completeWithError() 處理
		if (acceptHeader != null && acceptHeader.contains("text/event-stream")) {
			ex.printStackTrace(); // 或 log.error(...)
			return null;
		}

		// 否則仍走統一錯誤格式回傳
		String errorMessage = ex.toString();
		switch (ex.getClass().getSimpleName()) {
		case "MethodArgumentTypeMismatchException":
			errorMessage = "參數錯誤(" + ex.getClass().getSimpleName() + ")";
			break;
		case "NoResourceFoundException":
			errorMessage = "查無網頁(" + ex.getClass().getSimpleName() + ")";
			break;
		}

		return ResponseEntity.badRequest().body(ApiResponse.error(errorMessage));
	}

	@ExceptionHandler(BadNoteException.class)
	public ResponseEntity<ApiResponse<Object>> handleBadNote(BadNoteException e) {
		// 回傳乾淨一致的錯誤格式（使用你自己的 ApiResponse 類）
		return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
	}
}