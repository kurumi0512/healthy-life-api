package com.example.demo.service;

import java.util.List;
import java.util.Map;

//食物建議與蛋白質計算服務介面
public interface FoodService {

	// 根據體重與活動等級（level）計算每日蛋白質攝取目標
	double calculateProteinTarget(double weightKg, String level);

	// 根據目標蛋白質與是否純素，回傳食物建議清單（
	List<Map<String, Object>> getSuggestions(double targetProtein, boolean isVegan);

	// 依據蛋白質目標與是否純素，產生三餐建議菜單
	Map<String, List<Map<String, Object>>> getMealPlan(double targetProtein, boolean isVegan);
}