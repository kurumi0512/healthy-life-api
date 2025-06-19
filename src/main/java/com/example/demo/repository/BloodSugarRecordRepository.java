package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.BloodSugarRecord;
import com.example.demo.model.entity.User;

// 操作 BloodSugarRecord（血糖紀錄）資料表的 Repository 介面
public interface BloodSugarRecordRepository extends JpaRepository<BloodSugarRecord, Integer> {

	// 根據 User 物件查詢所有血糖紀錄（不排序）
	List<BloodSugarRecord> findByUser(User user);

	// 根據 userId 查詢血糖紀錄，依記錄日期由新到舊排序（常用）
	List<BloodSugarRecord> findByUserIdOrderByRecordDateDesc(Integer userId);

	// 根據 User 物件查詢，並依記錄日期由新到舊排序
	List<BloodSugarRecord> findByUserOrderByRecordDateDesc(User user);

	// 根據帳號 ID 查詢該帳號下所有血糖紀錄（依記錄日期由新到舊排序）
	List<BloodSugarRecord> findByUser_Account_IdOrderByRecordDateDesc(Integer accountId);

	// 查詢某帳號最新一筆血糖紀錄（記錄日期最新的那筆）
	BloodSugarRecord findTopByUser_Account_IdOrderByRecordDateDesc(Integer accountId);
}
