package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.WeightRecord;

//操作 WeightRecord（體重紀錄）資料表的 Repository 介面
public interface WeightRecordRepository extends JpaRepository<WeightRecord, Integer> {

	// 根據帳號 ID 查詢所有體重紀錄，依照記錄日期（使用者輸入的日期）由新到舊排序
	List<WeightRecord> findByUser_Account_IdOrderByRecordDateDesc(Integer accountId);

	// 根據 userId 查詢最近 5 筆體重紀錄，依照建立時間由新到舊排序（適合首頁顯示）
	List<WeightRecord> findTop5ByUser_IdOrderByCreatedAtDesc(Integer userId);

	// 查詢某使用者最新的一筆體重紀錄（依建立時間排序，通常用於預設值或一鍵填入）
	WeightRecord findTopByUser_IdOrderByCreatedAtDesc(Integer userId);
}
