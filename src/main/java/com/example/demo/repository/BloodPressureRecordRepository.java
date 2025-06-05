package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.BloodPressureRecord;

public interface BloodPressureRecordRepository extends JpaRepository<BloodPressureRecord, Integer> {
	List<BloodPressureRecord> findTop5ByUser_IdOrderByCreatedAtDesc(Integer userId);

	List<BloodPressureRecord> findByUser_Account_IdOrderByRecordDateDesc(Integer accountId);

	BloodPressureRecord findFirstByUser_Account_IdOrderByRecordDateDesc(Integer accountId); // 根據 accountId 取得紀錄日期最新的一筆

}