package com.example.demo.exception;

public class BadNoteException extends RuntimeException {
	public BadNoteException(String message) {
		super(message);
	}
}