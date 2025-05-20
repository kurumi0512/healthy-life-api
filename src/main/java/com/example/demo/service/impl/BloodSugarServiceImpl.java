package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.entity.BloodSugarRecord;
import com.example.demo.model.entity.User;
import com.example.demo.repository.BloodSugarRepository;
import com.example.demo.service.BloodSugarService;

@Service
public class BloodSugarServiceImpl implements BloodSugarService {

	@Autowired
	private BloodSugarRepository bloodSugarRepository;

	@Override
	public void save(BloodSugarRecord record) {
		bloodSugarRepository.save(record);
	}

	@Override
	public List<BloodSugarRecord> findByUser(User user) {
		return bloodSugarRepository.findByUser(user);
	}
}