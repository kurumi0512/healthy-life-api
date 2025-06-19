package com.example.demo.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * ✅ 不雅詞過濾工具類（BadWordFilter） - 用於偵測與替換留言、輸入內容中的髒話或不當詞彙 - 可用於留言板、聊天室、健康建議備註欄等地方
 */
public class BadWordFilter {

	// 定義髒話關鍵字清單（可擴充）
	private static final List<String> BAD_WORDS = Arrays.asList("幹", "靠北", "垃圾", "白癡", "87", "腦殘", "機掰", "媽的", "三小",
			"王八", "龜兒子", "屁", "死.*蛋", "智障", "他媽的");

	// 將髒話字串轉換為不分大小寫的正則 Pattern，供檢查與替換使用
	private static final List<Pattern> BAD_WORD_PATTERNS = BAD_WORDS.stream()
			.map(word -> Pattern.compile(word, Pattern.CASE_INSENSITIVE)) // 忽略大小寫
			.toList();

	/**
	 * ✅ 檢查字串中是否含有不雅詞
	 * 
	 * @param input 使用者輸入內容
	 * @return true = 有髒話，false = 沒有
	 */
	public static boolean containsBadWord(String input) {
		if (input == null || input.isBlank())
			return false;
		return BAD_WORD_PATTERNS.stream().anyMatch(p -> p.matcher(input).find());
	}

	/**
	 * ✅ 將不雅詞替換為 "***"
	 * 
	 * @param input 原始輸入字串
	 * @return 替換後的字串，若為 null 則回傳 null
	 */
	public static String replaceBadWords(String input) {
		if (input == null)
			return null;
		String result = input;
		for (Pattern pattern : BAD_WORD_PATTERNS) {
			result = pattern.matcher(result).replaceAll("***");
		}
		return result;
	}
}