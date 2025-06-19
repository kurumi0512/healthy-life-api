package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.Food;

//操作 Food（食物資料）資料表的 Repository 介面
public interface FoodRepository extends JpaRepository<Food, Integer> {

	// 根據是否為純素查詢食物（true = 純素, false = 非純素）
	List<Food> findByIsVegan(boolean isVegan);
}
