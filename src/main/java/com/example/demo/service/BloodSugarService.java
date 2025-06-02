package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.BloodSugarRecordDTO;

public interface BloodSugarService {

	// 改成接收 DTO（由 Controller 傳入）
	void save(BloodSugarRecordDTO dto);

	// 改成回傳 DTO（回傳給前端）
	List<BloodSugarRecordDTO> findByUserId(Integer accountId); // 查某帳號的所有紀錄

	List<BloodSugarRecordDTO> findRecentByUserId(Integer accountId, int limit); // 查近 n 筆紀錄

	BloodSugarRecordDTO findById(Integer id); // 查單筆（用於前端編輯畫面）

	void delete(Integer id);

	// 範例：條件查詢（保留）
	// List<BloodSugarRecordDTO> findByUserIdAndDateRange(Integer userId, LocalDate
	// startDate, LocalDate endDate);
}