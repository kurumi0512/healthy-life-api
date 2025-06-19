package com.example.demo.exception;

//自訂例外類別：CertException
//繼承自 Exception，表示這是一個「受檢例外」（Checked Exception）
//通常用於「憑證錯誤」等需要特別處理的情況，例如驗證失敗、簽章錯誤等

public class CertException extends Exception {
	public CertException(String message) {
		super(message); // 把錯誤訊息存進 Exception
	}
}
