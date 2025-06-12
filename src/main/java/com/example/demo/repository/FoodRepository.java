package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.Food;

public interface FoodRepository extends JpaRepository<Food, Integer> {
	List<Food> findByIsVegan(boolean isVegan);
}
