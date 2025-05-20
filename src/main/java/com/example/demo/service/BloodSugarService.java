package com.example.demo.service;

import java.util.List;

import com.example.demo.model.entity.BloodSugarRecord;
import com.example.demo.model.entity.User;

public interface BloodSugarService {
	void save(BloodSugarRecord record);

	List<BloodSugarRecord> findByUser(User user);
}