package com.example.demo.exception;

// 自定義例外類別：用來處理「備註內容不符合規範」的情況
public class BadNoteException extends RuntimeException {

	// 建構式：接收錯誤訊息並傳給父類別 RuntimeException
	public BadNoteException(String message) {
		super(message);
	}
}