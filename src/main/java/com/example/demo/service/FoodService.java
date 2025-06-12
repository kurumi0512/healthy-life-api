package com.example.demo.service;

import java.util.List;
import java.util.Map;

public interface FoodService {

	double calculateProteinTarget(double weightKg, String level);

	List<Map<String, Object>> getSuggestions(double targetProtein, boolean isVegan);

	Map<String, List<Map<String, Object>>> getMealPlan(double targetProtein, boolean isVegan);
}