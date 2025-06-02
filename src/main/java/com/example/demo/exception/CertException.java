package com.example.demo.exception;

//宣告一個公開類別 CertException，它是繼承自 Exception（受檢例外 Checked Exception）。
//表示這是需要用 try-catch 處理或在方法上宣告 throws 的例外。

public class CertException extends Exception {
	public CertException(String message) {
		super(message);
	}
}
