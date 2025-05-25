package com.example.demo.service;

import java.util.List;

import com.example.demo.model.entity.BloodSugarRecord;
import com.example.demo.model.entity.User;

public interface BloodSugarService {
	void save(BloodSugarRecord record);

	List<BloodSugarRecord> findByUser(User user); // 查所有血糖紀錄

	List<BloodSugarRecord> findRecentByUserId(Integer userId, int limit); // 查近 n 筆

	BloodSugarRecord findById(Integer id);

	void delete(Integer id);
}