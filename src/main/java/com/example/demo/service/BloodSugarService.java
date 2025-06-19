package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.BloodSugarRecordDTO;

//血糖紀錄相關功能的服務層介面
public interface BloodSugarService {

	// 儲存一筆血糖紀錄（接收來自 Controller 的 DTO）
	void save(BloodSugarRecordDTO dto);

	// 查詢某帳號的所有血糖紀錄（依記錄日期排序）
	List<BloodSugarRecordDTO> findByUserId(Integer accountId);

	// 查詢某帳號最近 n 筆血糖紀錄（依記錄日期由新到舊）
	List<BloodSugarRecordDTO> findRecentByUserId(Integer accountId, int limit);

	// 查詢單筆血糖紀錄（用於前端載入編輯用）
	BloodSugarRecordDTO findById(Integer id);

	// 刪除指定的血糖紀錄
	void delete(Integer id);

	// 查詢某帳號最新一筆血糖紀錄（記錄日期最新，用於預設值）
	BloodSugarRecordDTO findLatestByUserId(Integer accountId);

}