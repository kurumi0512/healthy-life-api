package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.Collections;
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

	private final FoodRepository foodRepo; // 操作 food 資料表
	private final FoodLimitRepository foodLimitRepo; // 操作 food_limit 限量表

	public FoodServiceImpl(FoodRepository foodRepo, FoodLimitRepository foodLimitRepo) {
		this.foodRepo = foodRepo;
		this.foodLimitRepo = foodLimitRepo;
	}

	// 計算每日蛋白質攝取目標
	@Override
	public double calculateProteinTarget(double weightKg, String level) {
		return switch (level) {
		case "athlete" -> weightKg * 2.0;
		case "active" -> weightKg * 1.5;
		default -> weightKg * 1.0;
		};
	}

	// 建議食物清單，依蛋白質需求與是否為素食篩選
	@Override
	public List<Map<String, Object>> getSuggestions(double targetProtein, boolean isVegan) {
		// 取得食物資料，若是素食者則只取素食
		List<Food> foods = isVegan ? foodRepo.findByIsVegan(true) : foodRepo.findAll();

		// 設定類別的建議順序（數字越小優先）
		Map<String, Integer> categoryPriority = Map.of("肉類", 1, "魚類", 2, "蛋類", 3, "豆類", 4, "乳製品", 5, "堅果", 6, "穀類", 7);

		// 排序邏輯：先依類別排序，再依蛋白質高低排序
		foods.sort((a, b) -> {
			int p1 = categoryPriority.getOrDefault(a.getCategory(), 99);
			int p2 = categoryPriority.getOrDefault(b.getCategory(), 99);
			if (p1 != p2)
				return Integer.compare(p1, p2);
			return Double.compare(b.getProteinPer100g(), a.getProteinPer100g());
		});

		// 每類別內食物打亂，增加多樣性
		Map<String, List<Food>> grouped = new HashMap<>();
		for (Food food : foods) {
			grouped.computeIfAbsent(food.getCategory(), k -> new ArrayList<>()).add(food);
		}
		foods.clear();
		grouped.forEach((category, list) -> {
			Collections.shuffle(list);
			foods.addAll(list);
		});

		// 開始組合建議清單
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Integer> categoryCount = new HashMap<>();
		double remaining = targetProtein;

		for (Food food : foods) {
			if (remaining <= 0)
				break; // 達標即結束

			double proteinPer100g = food.getProteinPer100g();
			if (proteinPer100g <= 0)
				continue;

			String category = food.getCategory();
			if (categoryCount.getOrDefault(category, 0) >= 1)
				continue; // 每類限一種
			categoryCount.put(category, 1);

			// 查詢該食物的最大建議攝取量（g）
			var limitOpt = foodLimitRepo.findByFoodId(food.getId());
			double maxPortion = limitOpt.map(FoodLimit::getMaxPortionG).orElse(120.0);

			// 根據蛋白質目標，計算建議份量（g）
			double suggestedPortion = (remaining / proteinPer100g) * 100;
			double portion = Math.min(suggestedPortion, maxPortion);
			double protein = (proteinPer100g * portion) / 100;

			// 若份量太少或蛋白質為 0，略過
			if (portion < 10 || protein <= 0)
				continue;

			// 組成單筆建議項目
			Map<String, Object> item = new HashMap<>();
			item.put("name", food.getName());
			item.put("amount_g", Math.round(portion));
			item.put("protein", Math.round(protein * 10.0) / 10.0);
			item.put("category", category);
			item.put("note", limitOpt.map(FoodLimit::getNote).orElse(""));
			item.put("protein_per_100g", proteinPer100g);

			result.add(item);
			remaining -= protein; // 減去已建議的蛋白質量
		}

		return result;
	}

	// 根據蛋白質需求產生三餐建議（平均分配）
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