package com.example.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.aop.CheckNotes;
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
	private BloodSugarMapper bloodSugarMapper; // DTO ↔ Entity 轉換工具

	@Autowired
	private BloodSugarRecordRepository bloodSugarRepository; // 操作 blood_sugar_record 資料表

	@Autowired
	private UserRepository userRepository; // 操作 user 資料表

	// 新增或更新一筆血糖紀錄
	@Override
	@CheckNotes // 自訂 AOP，檢查備註欄位內容
	public void save(BloodSugarRecordDTO dto) {
		// 根據 accountId 找出對應的 User
		User user = userRepository.findByAccount_Id(dto.getAccountId())
				.orElseThrow(() -> new RuntimeException("找不到使用者"));

		// 將 DTO 轉成 Entity，補上預設值（如當天日期）
		BloodSugarRecord record = bloodSugarMapper.toEntityWithDefaults(dto);
		record.setUser(user); // 設定關聯的使用者

		// 驗證資料合法性
		validateBloodSugar(record);

		// 存入資料庫
		bloodSugarRepository.save(record);
	}

	// 查詢該帳號的所有血糖紀錄（依照日期倒序）
	@Override
	public List<BloodSugarRecordDTO> findByUserId(Integer accountId) {
		List<BloodSugarRecord> records = bloodSugarRepository.findByUser_Account_IdOrderByRecordDateDesc(accountId);
		return records.stream().map(bloodSugarMapper::toDto).collect(Collectors.toList());
	}

	// 查詢最近 N 筆血糖紀錄
	@Override
	public List<BloodSugarRecordDTO> findRecentByUserId(Integer accountId, int limit) {
		List<BloodSugarRecord> records = bloodSugarRepository.findByUserIdOrderByRecordDateDesc(accountId).stream()
				.limit(limit).toList();

		return records.stream().map(bloodSugarMapper::toDto).collect(Collectors.toList());
	}

	// 根據 id 查詢單筆血糖紀錄
	@Override
	public BloodSugarRecordDTO findById(Integer id) {
		BloodSugarRecord record = bloodSugarRepository.findById(id).orElseThrow(() -> new RuntimeException("紀錄不存在"));
		return bloodSugarMapper.toDto(record);
	}

	// 刪除一筆血糖紀錄
	@Override
	public void delete(Integer id) {
		bloodSugarRepository.deleteById(id);
	}

	// 驗證血糖紀錄是否合理
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

	// 查詢最新一筆血糖紀錄
	@Override
	public BloodSugarRecordDTO findLatestByUserId(Integer accountId) {
		BloodSugarRecord latest = bloodSugarRepository.findTopByUser_Account_IdOrderByRecordDateDesc(accountId);
		return latest != null ? bloodSugarMapper.toDto(latest) : null;
	}
}