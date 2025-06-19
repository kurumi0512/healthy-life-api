package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.BloodPressureRecord;

//操作 BloodPressureRecord（血壓紀錄）資料表的 Repository 介面
public interface BloodPressureRecordRepository extends JpaRepository<BloodPressureRecord, Integer> {

	// 查詢某位使用者最近的 5 筆血壓紀錄（依建立時間由新到舊）
	List<BloodPressureRecord> findTop5ByUser_IdOrderByCreatedAtDesc(Integer userId);

	// 根據帳號 ID 查詢所有血壓紀錄（依記錄日期由新到舊排序）
	List<BloodPressureRecord> findByUser_Account_IdOrderByRecordDateDesc(Integer accountId);

	// 根據帳號 ID 查詢血壓紀錄中「記錄日期最新」的一筆資料
	BloodPressureRecord findFirstByUser_Account_IdOrderByRecordDateDesc(Integer accountId);

}