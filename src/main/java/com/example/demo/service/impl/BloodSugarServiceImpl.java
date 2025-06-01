package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.BloodSugarMapper;
import com.example.demo.model.entity.BloodSugarRecord;
import com.example.demo.model.entity.User;
import com.example.demo.repository.BloodSugarRecordRepository;
import com.example.demo.service.BloodSugarService;

@Service
public class BloodSugarServiceImpl implements BloodSugarService {

	@Autowired
	private BloodSugarMapper bloodSugarMapper;

	@Autowired
	private BloodSugarRecordRepository bloodSugarRepository;

	@Override
	public void save(BloodSugarRecord record) {
		validateBloodSugar(record);
		bloodSugarRepository.save(record);
	}

	@Override
	public List<BloodSugarRecord> findByUser(User user) {
		return bloodSugarRepository.findByUserOrderByRecordDateDesc(user);
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

//	@Override
//	public List<BloodSugarRecordDTO> findByUserIdAndDateRange(Integer userId, LocalDate startDate, LocalDate endDate) {
//		List<BloodSugarRecord> list = bloodSugarRepository.findByUserIdAndDateRange(userId, startDate, endDate);
//		return list.stream().map(bloodSugarMapper::toDto).collect(Collectors.toList());
//	}

	private void validateBloodSugar(BloodSugarRecord record) {
		if (record.getFasting() < 30 || record.getFasting() > 250) {
			throw new IllegalArgumentException("餐前血糖值必須在 30 到 250 mg/dL 之間");
		}
		if (record.getPostMeal() < 30 || record.getPostMeal() > 250) {
			throw new IllegalArgumentException("餐後血糖值必須在 30 到 250 mg/dL 之間");
		}
		if (record.getNotes() != null && record.getNotes().length() > 50) {
			throw new IllegalArgumentException("備註最多只能輸入 50 個字");
		}
	}

}