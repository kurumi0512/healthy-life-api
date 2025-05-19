package com.example.demo.util;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class HashUtil {

	public static String generateSalt() {
		try {
			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[16];
			random.nextBytes(salt);
			return bytesToHex(salt);
		} catch (Exception e) {
			throw new RuntimeException("生成 Salt 失敗", e);
		}
	}

	public static String hashPassword(String password, String salt) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = md.digest((password + salt).getBytes());
			return bytesToHex(hashBytes);
		} catch (Exception e) {
			throw new RuntimeException("產生雜湊失敗", e);
		}
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}