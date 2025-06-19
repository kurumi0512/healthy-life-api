package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.AdviceHistory;

//操作 AdviceHistory（AI 建議紀錄）資料表的 Repository 介面
public interface AdviceHistoryRepository extends JpaRepository<AdviceHistory, Integer> {

	// 查詢某位使用者的所有建議歷史，依建立時間從新到舊排序
	List<AdviceHistory> findByUser_IdOrderByCreatedAtDesc(Integer userId);

	// 查詢某位使用者最新的一筆建議紀錄（建立時間最新）
	AdviceHistory findTopByUser_IdOrderByCreatedAtDesc(Integer userId);
}
