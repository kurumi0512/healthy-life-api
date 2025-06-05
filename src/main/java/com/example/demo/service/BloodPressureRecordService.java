package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.BloodPressureRecordDTO;

public interface BloodPressureRecordService {
	void saveRecord(BloodPressureRecordDTO dto);

	List<BloodPressureRecordDTO> getRecentRecords(Integer accountId);

	List<BloodPressureRecordDTO> getAllRecords(Integer accountId);

	void updateRecord(BloodPressureRecordDTO dto);

	void deleteRecord(Integer recordId, Integer accountId);

	BloodPressureRecordDTO findLatestByUserId(Integer accountId);
}