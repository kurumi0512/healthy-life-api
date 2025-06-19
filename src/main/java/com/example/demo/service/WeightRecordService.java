package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.dto.WeightRecordDTO;

//體重紀錄的服務介面
//處理儲存、查詢、更新、刪除等邏輯
public interface WeightRecordService {

	// 儲存新的體重紀錄
	void saveRecord(WeightRecordDTO dto);

	// 依帳號 ID 查詢所有體重紀錄
	List<WeightRecordDTO> getRecordsByAccountId(Integer accountId);

	// 更新一筆體重紀錄
	void updateRecord(WeightRecordDTO dto);

	// 刪除體重紀錄
	void deleteRecord(Integer recordId, Integer accountId);

	// 取得最近 5 筆體重紀錄
	List<WeightRecordDTO> getRecent5RecordsByAccountId(Integer accountId);

	// 取得最新一筆體重紀錄
	Optional<WeightRecordDTO> getLatestRecordByAccountId(Integer accountId);
}