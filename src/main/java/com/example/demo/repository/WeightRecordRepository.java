package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.WeightRecord;

public interface WeightRecordRepository extends JpaRepository<WeightRecord, Integer> {
	List<WeightRecord> findByUser_Account_IdOrderByRecordDateAsc(Integer accountId);

	List<WeightRecord> findTop5ByUser_IdOrderByCreatedAtDesc(Integer userId);

//	// 查詢最近 5 筆體重紀錄
//	@Query("SELECT w FROM WeightRecord w WHERE w.user.id = :userId ORDER BY w.recordDate DESC")
//	List<WeightRecord> findRecent5ByUserId(Integer userId);
//
//	// 查詢最近 N 天的體重紀錄
//	@Query("SELECT w FROM WeightRecord w WHERE w.user.id = :userId AND w.recordDate >= CURRENT_DATE - :days ORDER BY w.recordDate ASC")
//	List<WeightRecord> findByUserIdInRecentDays(Integer userId, int days);
//
//	// 查某時間區間的體重紀錄
//	@Query("SELECT w FROM WeightRecord w WHERE w.user.id = :userId AND w.recordDate BETWEEN :startDate AND :endDate ORDER BY w.recordDate ASC")
//	List<WeightRecord> findByUserIdAndDateRange(@Param("userId") Integer userId,
//			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
