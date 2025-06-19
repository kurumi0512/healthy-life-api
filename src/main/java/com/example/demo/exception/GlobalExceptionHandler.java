package com.example.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

//全域例外處理類別：處理所有 Controller 層發生的例外，避免重複寫 try-catch
@ControllerAdvice // 全域異常處理註解
public class GlobalExceptionHandler {

	// 處理所有 Exception 類型的錯誤（保險處理）
	@ExceptionHandler(Exception.class)
	public Object handleException(HttpServletRequest request, Exception ex) {
		String acceptHeader = request.getHeader("Accept");

		// 若是 SSE 請求（即 text/event-stream），不回傳錯誤 JSON，交給 emitter.completeWithError 處理
		if (acceptHeader != null && acceptHeader.contains("text/event-stream")) {
			ex.printStackTrace(); // 或 log.error(...)
			return null;
		}

		// 一般情況下，根據錯誤類別，組出一致的錯誤訊息
		String errorMessage = ex.toString();
		switch (ex.getClass().getSimpleName()) {
		case "MethodArgumentTypeMismatchException":
			errorMessage = "參數錯誤(" + ex.getClass().getSimpleName() + ")";
			break;
		case "NoResourceFoundException":
			errorMessage = "查無網頁(" + ex.getClass().getSimpleName() + ")";
			break;
		}

		// 使用自定義的 ApiResponse 統一錯誤回應格式
		return ResponseEntity.badRequest().body(ApiResponse.error(errorMessage));
	}

	// 處理自訂的 BadNoteException（例如：備註內容不符限制）
	@ExceptionHandler(BadNoteException.class)
	public ResponseEntity<ApiResponse<Object>> handleBadNote(BadNoteException e) {
		return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
	}
}