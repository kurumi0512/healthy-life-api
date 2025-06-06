package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.dto.WeightRecordDTO;

public interface WeightRecordService {
	void saveRecord(WeightRecordDTO dto);

	List<WeightRecordDTO> getRecordsByAccountId(Integer accountId);

	void updateRecord(WeightRecordDTO dto);

	void deleteRecord(Integer recordId, Integer accountId);

	List<WeightRecordDTO> getRecent5RecordsByAccountId(Integer accountId);

	Optional<WeightRecordDTO> getLatestRecordByAccountId(Integer accountId);
}