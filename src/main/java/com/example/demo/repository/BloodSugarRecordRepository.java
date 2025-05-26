package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.BloodSugarRecord;
import com.example.demo.model.entity.User;

public interface BloodSugarRecordRepository extends JpaRepository<BloodSugarRecord, Integer> {
	List<BloodSugarRecord> findByUser(User user);

	List<BloodSugarRecord> findByUserIdOrderByRecordDateDesc(Integer userId);

//	// 查詢最近 5 筆血糖紀錄
//	@Query("SELECT b FROM BloodSugarRecord b WHERE b.user.id = :userId ORDER BY b.recordDate DESC")
//	List<BloodSugarRecord> findRecent5ByUserId(Integer userId);
//
//	// 查詢最近 N 天的血糖紀錄
//	@Query("SELECT b FROM BloodSugarRecord b WHERE b.user.id = :userId AND b.recordDate >= CURRENT_DATE - :days ORDER BY b.recordDate ASC")
//	List<BloodSugarRecord> findByUserIdInRecentDays(Integer userId, int days);
//
//	// 查詢某日期區間內的血糖紀錄
//	@Query("SELECT b FROM BloodSugarRecord b WHERE b.user.id = :userId AND b.recordDate BETWEEN :startDate AND :endDate ORDER BY b.recordDate ASC")
//	List<BloodSugarRecord> findByUserIdAndDateRange(@Param("userId") Integer userId,
//			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
