package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.WeightRecord;

public interface WeightRecordRepository extends JpaRepository<WeightRecord, Integer> {
	List<WeightRecord> findByUser_AccountIdOrderByRecordDateAsc(Integer accountId);
}
