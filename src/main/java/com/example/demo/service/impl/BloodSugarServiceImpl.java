package com.example.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.BloodSugarMapper;
import com.example.demo.model.dto.BloodSugarRecordDTO;
import com.example.demo.model.entity.BloodSugarRecord;
import com.example.demo.model.entity.User;
import com.example.demo.repository.BloodSugarRecordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BloodSugarService;

@Service
public class BloodSugarServiceImpl implements BloodSugarService {

	@Autowired
	private BloodSugarMapper bloodSugarMapper;

	@Autowired
	private BloodSugarRecordRepository bloodSugarRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public void save(BloodSugarRecordDTO dto) {
		User user = userRepository.findByAccount_Id(dto.getAccountId())
				.orElseThrow(() -> new RuntimeException("找不到使用者"));

		BloodSugarRecord record = bloodSugarMapper.toEntityWithDefaults(dto);
		record.setUser(user);

		validateBloodSugar(record);
		bloodSugarRepository.save(record);
	}

	@Override
	public List<BloodSugarRecordDTO> findByUserId(Integer accountId) {
		List<BloodSugarRecord> records = bloodSugarRepository.findByUser_Account_IdOrderByRecordDateDesc(accountId);
		return records.stream().map(bloodSugarMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public List<BloodSugarRecordDTO> findRecentByUserId(Integer accountId, int limit) {
		List<BloodSugarRecord> records = bloodSugarRepository.findByUserIdOrderByRecordDateDesc(accountId).stream()
				.limit(limit).toList();

		return records.stream().map(bloodSugarMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public BloodSugarRecordDTO findById(Integer id) {
		BloodSugarRecord record = bloodSugarRepository.findById(id).orElseThrow(() -> new RuntimeException("紀錄不存在"));
		return bloodSugarMapper.toDto(record);
	}

	@Override
	public void delete(Integer id) {
		bloodSugarRepository.deleteById(id);
	}

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

	@Override
	public BloodSugarRecordDTO findLatestByUserId(Integer accountId) {
		BloodSugarRecord latest = bloodSugarRepository.findTopByUser_Account_IdOrderByRecordDateDesc(accountId);
		return latest != null ? bloodSugarMapper.toDto(latest) : null;
	}
}