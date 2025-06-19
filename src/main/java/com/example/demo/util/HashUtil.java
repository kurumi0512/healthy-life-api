package com.example.demo.util;

import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * ✅ 密碼雜湊工具類（HashUtil） - 用於產生隨機 Salt 與 SHA-256 雜湊加密密碼 - 目的：強化密碼儲存安全，防止彩虹表攻擊
 */
public class HashUtil {

	/**
	 * ✅ 產生隨機 Salt（16 位元組，回傳為 32 字元的 16 進位字串）
	 * 
	 * @return 十六進位表示的隨機 Salt 字串
	 */
	public static String generateSalt() {
		try {
			SecureRandom random = new SecureRandom(); // 安全隨機數生成器
			byte[] salt = new byte[16]; // 128 bits
			random.nextBytes(salt); // 填入隨機位元組
			return bytesToHex(salt); // 轉成十六進位字串
		} catch (Exception e) {
			throw new RuntimeException("生成 Salt 失敗", e);
		}
	}

	/**
	 * ✅ 將密碼與 salt 合併後使用 SHA-256 雜湊，產生不可逆加密字串
	 * 
	 * @param password 原始密碼
	 * @param salt     加鹽值（每個帳號唯一）
	 * @return SHA-256 雜湊結果（十六進位字串）
	 */
	public static String hashPassword(String password, String salt) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256"); // 使用 SHA-256 演算法
			byte[] hashBytes = md.digest((password + salt).getBytes()); // 雜湊計算
			return bytesToHex(hashBytes); // 轉十六進位
		} catch (Exception e) {
			throw new RuntimeException("產生雜湊失敗", e);
		}
	}

	/**
	 * ✅ 將位元組陣列轉為十六進位字串
	 * 
	 * @param bytes 要轉換的 byte 陣列
	 * @return 對應的十六進位字串
	 */
	private static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b)); // 補零輸出兩位數十六進位
		}
		return sb.toString();
	}
}