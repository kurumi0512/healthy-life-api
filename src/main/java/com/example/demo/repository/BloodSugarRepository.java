package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.BloodSugarRecord;
import com.example.demo.model.entity.User;

@Repository
public interface BloodSugarRepository extends JpaRepository<BloodSugarRecord, Integer> {

	// 自動依使用者查找紀錄
	List<BloodSugarRecord> findByUser(User user);
}
