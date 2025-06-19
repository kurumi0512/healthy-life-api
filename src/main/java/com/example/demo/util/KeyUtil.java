package com.example.demo.util;

import java.math.BigInteger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * ✅ KeyUtil - 驗證碼工具類 實作 TOTP（Time-based One-Time Password）產生器，支援 HMAC 演算法
 * 可用於雙因子驗證（2FA）、登入驗證碼產生
 */
public class KeyUtil {

	/**
	 * ✅ 根據 Secret 與時間區段產生 TOTP 驗證碼
	 *
	 * @param secret    金鑰（字串格式）
	 * @param interval  時間區段（如當前時間毫秒 / 30_000）
	 * @param algorithm 使用的 HMAC 演算法，例如 "HmacSHA1", "HmacSHA256", "HmacSHA512"
	 * @return 六位數字驗證碼（補零格式）
	 * @throws Exception 若演算法或初始化失敗
	 */
	public static String generateTOTP(String secret, long interval, String algorithm) throws Exception {
		// 將金鑰字串轉換成 byte 陣列
		byte[] key = secret.getBytes();

		// 將時間區段（例如：當前秒數 / 30）轉為 byte 陣列
		byte[] data = BigInteger.valueOf(interval).toByteArray();

		// 建立 MAC（訊息認證碼）實體，設定指定演算法
		Mac mac = Mac.getInstance(algorithm);
		SecretKeySpec spec = new SecretKeySpec(key, algorithm);
		mac.init(spec);

		// 計算 HMAC 值（雜湊）
		byte[] hash = mac.doFinal(data);

		// 動態截斷（Dynamic Truncation）處理：取 hash 最後一個 byte 的低 4 bits 作為 offset
		int offset = hash[hash.length - 1] & 0xf;

		// 取出連續 4 個 byte 並組成 31 位整數（最高位元需為 0）
		int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16)
				| ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);

		// 對 1000000 取餘，取得 6 位數驗證碼
		int otp = binary % 1_000_000;

		// 不足六位數時前面補零（例如 000123）
		return String.format("%06d", otp);
	}
}