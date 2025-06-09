package com.example.demo.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class BadWordFilter {

	private static final List<String> BAD_WORDS = Arrays.asList("幹", "靠北", "垃圾", "白癡", "87", "腦殘", "機掰", "媽的", "三小",
			"王八", "龜兒子", "屁", "死.*蛋", "智障", "他媽的");

	private static final List<Pattern> BAD_WORD_PATTERNS = BAD_WORDS.stream()
			.map(word -> Pattern.compile(word, Pattern.CASE_INSENSITIVE)).toList();

	public static boolean containsBadWord(String input) {
		if (input == null || input.isBlank())
			return false;
		return BAD_WORD_PATTERNS.stream().anyMatch(p -> p.matcher(input).find());
	}

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
