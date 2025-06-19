package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.FoodLimit;

//操作 FoodLimit（食物攝取上限）資料表的 Repository 介面
public interface FoodLimitRepository extends JpaRepository<FoodLimit, Integer> {

	// 根據 foodId 查詢該食物的攝取上限（回傳 Optional，避免 null）
	Optional<FoodLimit> findByFoodId(Integer foodId);
}
