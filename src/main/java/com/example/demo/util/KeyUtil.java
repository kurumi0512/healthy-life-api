package com.example.demo.util;

import java.math.BigInteger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class KeyUtil {

	public static String generateTOTP(String secret, long interval, String algorithm) throws Exception {
		// 金鑰轉成 byte[]
		byte[] key = secret.getBytes();

		// 時間區段轉成 byte[]，例如：當前時間 / 30 秒
		byte[] data = BigInteger.valueOf(interval).toByteArray();

		// 設定 HMAC 演算法（HMACSHA1、HMACSHA256、HMACSHA512）
		Mac mac = Mac.getInstance(algorithm);
		SecretKeySpec spec = new SecretKeySpec(key, algorithm);
		mac.init(spec);

		// 計算 HMAC 結果
		byte[] hash = mac.doFinal(data);

		// 動態截取（dynamic truncation）取得 4 bytes 結果
		int offset = hash[hash.length - 1] & 0xf;
		int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16)
				| ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);

		// 對 1000000 取餘，產生 6 位數驗證碼
		int otp = binary % 1000000;

		// 不足 6 位補 0
		return String.format("%06d", otp);
	}
}