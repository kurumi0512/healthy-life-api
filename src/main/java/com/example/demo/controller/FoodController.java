package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.FoodService;

@RestController
@RequestMapping("/rest/protein")
@CrossOrigin
public class FoodController {

	private final FoodService foodService;

	// ✅ Constructor 注入 Service
	public FoodController(FoodService foodService) {
		this.foodService = foodService;
	}

	// ✅ POST 蛋白質建議 API
	@PostMapping("/advice")
	public Map<String, Object> getProteinAdvice(@RequestBody Map<String, Object> payload) {
		double weight = Double.parseDouble(payload.get("weight").toString());
		String level = payload.get("activityLevel").toString(); // normal | active | athlete
		boolean isVegan = Boolean.parseBoolean(payload.get("isVegan").toString());

		double target = foodService.calculateProteinTarget(weight, level);
		List<Map<String, Object>> suggestions = foodService.getSuggestions(target, isVegan);

		Map<String, Object> result = new HashMap<>();
		result.put("dailyProteinTarget", Math.round(target));
		result.put("suggestedFoods", suggestions);

		return result;
	}

	// ✅ POST 三餐蛋白質建議 API
	@PostMapping("/meal-plan")
	public Map<String, Object> getMealPlanAdvice(@RequestBody Map<String, Object> payload) {
		double weight = Double.parseDouble(payload.get("weight").toString());
		String level = payload.get("activityLevel").toString(); // normal | active | athlete
		boolean isVegan = Boolean.parseBoolean(payload.get("isVegan").toString());

		double target = foodService.calculateProteinTarget(weight, level);
		Map<String, List<Map<String, Object>>> mealPlan = foodService.getMealPlan(target, isVegan);

		Map<String, Object> result = new HashMap<>();
		result.put("dailyProteinTarget", Math.round(target));
		result.put("mealPlan", mealPlan); // 含 早餐／午餐／晚餐

		return result;
	}
}
