package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.BloodPressureRecord;

public interface BloodPressureRecordRepository extends JpaRepository<BloodPressureRecord, Integer> {
	List<BloodPressureRecord> findTop5ByUser_IdOrderByCreatedAtDesc(Integer userId);

//	// 查詢最近 5 筆血壓紀錄
//	@Query("SELECT b FROM BloodPressureRecord b WHERE b.user.id = :userId ORDER BY b.recordDate DESC")
//	List<BloodPressureRecord> findRecent5ByUserId(Integer userId);
//
//	// 查詢最近 N 天的血壓紀錄
//	@Query("SELECT b FROM BloodPressureRecord b WHERE b.user.id = :userId AND b.recordDate >= :startDate ORDER BY b.recordDate ASC")
//	List<BloodPressureRecord> findByUserIdInRecentDays(@Param("userId") Integer userId,
//			@Param("startDate") LocalDate startDate);
//
//	// 查詢某日期區間內的血壓紀錄
//	@Query("SELECT b FROM BloodPressureRecord b WHERE b.user.id = :userId AND b.recordDate BETWEEN :startDate AND :endDate ORDER BY b.recordDate ASC")
//	List<BloodPressureRecord> findByUserIdAndDateRange(@Param("userId") Integer userId,
//			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}