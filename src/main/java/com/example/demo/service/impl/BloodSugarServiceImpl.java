package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.entity.BloodSugarRecord;
import com.example.demo.model.entity.User;
import com.example.demo.repository.BloodSugarRecordRepository;
import com.example.demo.service.BloodSugarService;

@Service
public class BloodSugarServiceImpl implements BloodSugarService {

	@Autowired
	private BloodSugarRecordRepository bloodSugarRepository;

	@Override
	public void save(BloodSugarRecord record) {
		bloodSugarRepository.save(record);
	}

	@Override
	public List<BloodSugarRecord> findByUser(User user) {
		return bloodSugarRepository.findByUser(user);
	}

	@Override
	public List<BloodSugarRecord> findRecentByUserId(Integer userId, int limit) {
		return bloodSugarRepository.findByUserIdOrderByRecordDateDesc(userId).stream().limit(limit).toList();
	}

	@Override
	public BloodSugarRecord findById(Integer id) {
		return bloodSugarRepository.findById(id).orElse(null);
	}

	@Override
	public void delete(Integer id) {
		bloodSugarRepository.deleteById(id);
	}
}