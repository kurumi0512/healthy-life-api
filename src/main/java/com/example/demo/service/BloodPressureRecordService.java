package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.BloodPressureRecordDTO;

//血壓紀錄功能的服務層介面，負責 CRUD 與查詢邏輯
public interface BloodPressureRecordService {

	// 儲存血壓紀錄
	void saveRecord(BloodPressureRecordDTO dto);

	// 查詢某帳號最近幾筆血壓紀錄
	List<BloodPressureRecordDTO> getRecentRecords(Integer accountId);

	// 查詢某帳號所有血壓紀錄
	List<BloodPressureRecordDTO> getAllRecords(Integer accountId);

	// 更新血壓紀錄
	void updateRecord(BloodPressureRecordDTO dto);

	// 刪除指定血壓紀錄
	void deleteRecord(Integer recordId, Integer accountId);

	// 查詢某帳號最新一筆血壓紀錄（
	BloodPressureRecordDTO findLatestByUserId(Integer accountId);
}