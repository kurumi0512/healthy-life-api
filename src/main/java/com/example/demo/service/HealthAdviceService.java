package com.example.demo.service;

public interface HealthAdviceService {

	String generatePrompt(double height, double weight, int age, String goal);

	void saveAdviceRecord(Integer userId, String input, String advice);

	boolean shouldDisplayWord(String word, boolean[] insideThinkBlock);
}