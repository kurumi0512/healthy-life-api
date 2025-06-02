package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.BloodSugarRecord;
import com.example.demo.model.entity.User;

public interface BloodSugarRecordRepository extends JpaRepository<BloodSugarRecord, Integer> {
	List<BloodSugarRecord> findByUser(User user);

	List<BloodSugarRecord> findByUserIdOrderByRecordDateDesc(Integer userId);

	List<BloodSugarRecord> findByUserOrderByRecordDateDesc(User user);

	// ✅ 新增這個方法，支援血糖服務用 accountId 查紀錄
	List<BloodSugarRecord> findByUser_Account_IdOrderByRecordDateDesc(Integer accountId);

}
