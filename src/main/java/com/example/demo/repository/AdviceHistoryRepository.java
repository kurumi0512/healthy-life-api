package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.AdviceHistory;

public interface AdviceHistoryRepository extends JpaRepository<AdviceHistory, Integer> {

	// 查詢某位使用者的建議歷史（由新到舊）
	List<AdviceHistory> findByUser_IdOrderByCreatedAtDesc(Integer userId);
}
