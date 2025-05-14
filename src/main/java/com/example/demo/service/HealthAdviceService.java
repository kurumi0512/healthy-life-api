package com.example.demo.service;

public interface HealthAdviceService {
	String generatePrompt(double height, double weight, int age, String goal);

	boolean shouldDisplayWord(String word, boolean[] insideThinkBlock);
}