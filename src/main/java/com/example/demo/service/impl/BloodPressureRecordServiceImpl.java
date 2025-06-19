package com.example.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.aop.CheckNotes;
import com.example.demo.mapper.BloodPressureMapper;
import com.example.demo.model.dto.BloodPressureRecordDTO;
import com.example.demo.model.entity.BloodPressureRecord;
import com.example.demo.model.entity.User;
import com.example.demo.repository.BloodPressureRecordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BloodPressureRecordService;

@Service
public class BloodPressureRecordServiceImpl implements BloodPressureRecordService {

	@Autowired
	private BloodPressureMapper bloodPressureMapper; // DTO ↔ Entity 映射工具

	@Autowired
	private UserRepository userRepository; // 操作 user 資料表

	@Autowired
	private BloodPressureRecordRepository bpRecordRepository; // 操作 blood_pressure_record 資料表

	// 儲存一筆新的血壓紀錄
	@Override
	@CheckNotes // 自訂 AOP 驗證備註欄位
	public void saveRecord(BloodPressureRecordDTO dto) {
		User user = userRepository.findByAccount_Id(dto.getAccountId())
				.orElseThrow(() -> new RuntimeException("找不到使用者"));

		BloodPressureRecord record = bloodPressureMapper.toEntityWithDefaults(dto);
		record.setUser(user); // 綁定 user

		validateBloodPressure(record); // 驗證數值合法性
		bpRecordRepository.save(record);
		System.out.println("使用者選擇的日期：" + dto.getRecordDate());
	}

	// 查詢最近五筆血壓紀錄
	@Override
	public List<BloodPressureRecordDTO> getRecentRecords(Integer accountId) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("使用者不存在"));

		List<BloodPressureRecord> records = bpRecordRepository.findTop5ByUser_IdOrderByCreatedAtDesc(user.getId());
		List<BloodPressureRecordDTO> dtoList = bloodPressureMapper.toDtoList(records);

		// 額外補上 accountId（ModelMapper 不會處理這個欄位）
		dtoList.forEach(dto -> dto.setAccountId(accountId));
		return dtoList;
	}

	// 查詢所有血壓紀錄（依照紀錄日期倒序）
	@Override
	public List<BloodPressureRecordDTO> getAllRecords(Integer accountId) {
		List<BloodPressureRecord> records = bpRecordRepository.findByUser_Account_IdOrderByRecordDateDesc(accountId);
		return bloodPressureMapper.toDtoList(records);
	}

	// 編輯更新一筆血壓紀錄
	@Override
	@CheckNotes
	public void updateRecord(BloodPressureRecordDTO dto) {
		BloodPressureRecord record = bpRecordRepository.findById(dto.getRecordId())
				.orElseThrow(() -> new RuntimeException("紀錄不存在"));

		// 確保該紀錄是該使用者的
		if (!record.getUser().getAccount().getId().equals(dto.getAccountId())) {
			throw new RuntimeException("無權修改此紀錄");
		}

		// 更新欄位
		record.setSystolic(dto.getSystolic());
		record.setDiastolic(dto.getDiastolic());
		record.setNotes(dto.getNotes());
		if (dto.getRecordDate() != null) {
			record.setRecordDate(dto.getRecordDate()); // 若有填日期才更新
		}

		validateBloodPressure(record);
		bpRecordRepository.save(record);
	}

	// 刪除紀錄（含使用者驗證）
	@Override
	public void deleteRecord(Integer recordId, Integer accountId) {
		BloodPressureRecord record = bpRecordRepository.findById(recordId)
				.orElseThrow(() -> new RuntimeException("紀錄不存在"));

		if (!record.getUser().getAccount().getId().equals(accountId)) {
			throw new RuntimeException("無權刪除此紀錄");
		}

		bpRecordRepository.delete(record);
	}

	// 驗證血壓數值與備註內容是否合法
	private void validateBloodPressure(BloodPressureRecord record) {
		if (record.getSystolic() < 50 || record.getSystolic() > 250) {
			throw new IllegalArgumentException("收縮壓必須在 50～250 mmHg 範圍內");
		}
		if (record.getDiastolic() < 50 || record.getDiastolic() > 250) {
			throw new IllegalArgumentException("舒張壓必須在 50～250 mmHg 範圍內");
		}
		if (record.getNotes() != null && record.getNotes().length() > 50) {
			throw new IllegalArgumentException("備註最多 50 字");
		}
	}

	// 查詢最新一筆血壓紀錄
	@Override
	public BloodPressureRecordDTO findLatestByUserId(Integer accountId) {
		BloodPressureRecord latest = bpRecordRepository.findFirstByUser_Account_IdOrderByRecordDateDesc(accountId);
		return (latest != null) ? bloodPressureMapper.toDto(latest) : null;
	}
}
