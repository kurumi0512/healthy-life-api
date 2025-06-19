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

// 蛋白質建議 API 控制器
@RestController
@RequestMapping("/rest/protein")
@CrossOrigin
public class FoodController {

	private final FoodService foodService;

	// 建構式注入 FoodService
	public FoodController(FoodService foodService) {
		this.foodService = foodService;
	}

	// [POST] 取得蛋白質攝取建議（回傳建議食物清單）
	// 傳入參數：weight、activityLevel、isVegan
	// 回傳 dailyProteinTarget（目標蛋白質量）+ 建議食物建議清單
	@PostMapping("/advice")
	public Map<String, Object> getProteinAdvice(@RequestBody Map<String, Object> payload) {
		double weight = Double.parseDouble(payload.get("weight").toString());
		String level = payload.get("activityLevel").toString(); // normal | active | athlete
		boolean isVegan = Boolean.parseBoolean(payload.get("isVegan").toString());

		// 計算蛋白質建議攝取量
		double target = foodService.calculateProteinTarget(weight, level);
		// 根據攝取目標與飲食類型取得建議食物
		List<Map<String, Object>> suggestions = foodService.getSuggestions(target, isVegan);

		// 包裝回傳結果
		Map<String, Object> result = new HashMap<>();
		result.put("dailyProteinTarget", Math.round(target));
		result.put("suggestedFoods", suggestions);

		return result;
	}

	// [POST] 取得三餐建議（將蛋白質攝取目標分配成早餐／午餐／晚餐）
	// 傳入參數：weight、activityLevel、isVegan
	// 回傳 dailyProteinTarget + mealPlan（三餐建議）
	@PostMapping("/meal-plan")
	public Map<String, Object> getMealPlanAdvice(@RequestBody Map<String, Object> payload) {
		double weight = Double.parseDouble(payload.get("weight").toString());
		String level = payload.get("activityLevel").toString(); // normal | active | athlete
		boolean isVegan = Boolean.parseBoolean(payload.get("isVegan").toString());

		// 計算蛋白質建議攝取量
		double target = foodService.calculateProteinTarget(weight, level);
		// 根據攝取目標與飲食類型產生三餐建議
		Map<String, List<Map<String, Object>>> mealPlan = foodService.getMealPlan(target, isVegan);

		// 包裝回傳結果
		Map<String, Object> result = new HashMap<>();
		result.put("dailyProteinTarget", Math.round(target));
		result.put("mealPlan", mealPlan); // mealPlan 內含 breakfast、lunch、dinner

		return result;
	}
}
