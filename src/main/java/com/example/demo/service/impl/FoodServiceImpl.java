package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.model.entity.Food;
import com.example.demo.model.entity.FoodLimit;
import com.example.demo.repository.FoodLimitRepository;
import com.example.demo.repository.FoodRepository;
import com.example.demo.service.FoodService;

@Service
public class FoodServiceImpl implements FoodService {

	private final FoodRepository foodRepo;
	private final FoodLimitRepository foodLimitRepo;

	public FoodServiceImpl(FoodRepository foodRepo, FoodLimitRepository foodLimitRepo) {
		this.foodRepo = foodRepo;
		this.foodLimitRepo = foodLimitRepo;
	}

	@Override
	public double calculateProteinTarget(double weightKg, String level) {
		return switch (level) {
		case "athlete" -> weightKg * 2.0;
		case "active" -> weightKg * 1.5;
		default -> weightKg * 1.0;
		};
	}

	@Override
	public List<Map<String, Object>> getSuggestions(double targetProtein, boolean isVegan) {
		List<Food> foods = isVegan ? foodRepo.findByIsVegan(true) : foodRepo.findAll();

		// 1️⃣ 類別排序權重設定（數字越小，優先度越高）
		Map<String, Integer> categoryPriority = Map.of("肉類", 1, "魚類", 2, "蛋類", 3, "豆類", 4, "乳製品", 5, "堅果", 6, "穀類", 7);

		// 2️⃣ 多條件排序：先比類別優先，再比蛋白質含量
		foods.sort((a, b) -> {
			int p1 = categoryPriority.getOrDefault(a.getCategory(), 99);
			int p2 = categoryPriority.getOrDefault(b.getCategory(), 99);
			if (p1 != p2) {
				return Integer.compare(p1, p2); // 先比類別
			}
			return Double.compare(b.getProteinPer100g(), a.getProteinPer100g()); // 再比蛋白質含量
		});

		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Integer> categoryCount = new HashMap<>();
		double remaining = targetProtein;

		for (Food food : foods) {
			if (remaining <= 0)
				break;

			double proteinPer100g = food.getProteinPer100g();
			if (proteinPer100g <= 0)
				continue;

			String category = food.getCategory();
			if (categoryCount.getOrDefault(category, 0) >= 1)
				continue; // 每類最多推薦 1 種
			categoryCount.put(category, 1);

			var limitOpt = foodLimitRepo.findByFoodId(food.getId());
			double maxPortion = limitOpt.map(FoodLimit::getMaxPortionG).orElse(120.0);

			double suggestedPortion = (remaining / proteinPer100g) * 100;
			double portion = Math.min(suggestedPortion, maxPortion);
			double protein = (proteinPer100g * portion) / 100;

			if (protein <= 0)
				continue;

			Map<String, Object> item = new HashMap<>();
			item.put("name", food.getName());
			item.put("amount_g", Math.round(portion));
			item.put("protein", Math.round(protein * 10.0) / 10.0);
			item.put("category", category);
			item.put("note", limitOpt.map(FoodLimit::getNote).orElse(""));
			item.put("protein_per_100g", proteinPer100g);

			result.add(item);
			remaining -= protein;
		}

		return result;
	}

	@Override
	public Map<String, List<Map<String, Object>>> getMealPlan(double targetProtein, boolean isVegan) {
		double perMealTarget = targetProtein / 3;

		Map<String, List<Map<String, Object>>> mealPlan = new LinkedHashMap<>();
		mealPlan.put("早餐", getSuggestions(perMealTarget, isVegan));
		mealPlan.put("午餐", getSuggestions(perMealTarget, isVegan));
		mealPlan.put("晚餐", getSuggestions(perMealTarget, isVegan));

		return mealPlan;
	}
}