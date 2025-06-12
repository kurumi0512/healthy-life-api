package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.FoodLimit;

public interface FoodLimitRepository extends JpaRepository<FoodLimit, Integer> {
	Optional<FoodLimit> findByFoodId(Integer foodId);
}
