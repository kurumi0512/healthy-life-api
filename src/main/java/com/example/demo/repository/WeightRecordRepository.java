package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.WeightRecord;

public interface WeightRecordRepository extends JpaRepository<WeightRecord, Integer> {
	List<WeightRecord> findByUser_Account_IdOrderByRecordDateAsc(Integer accountId);

	List<WeightRecord> findTop5ByUser_IdOrderByCreatedAtDesc(Integer userId);
}
