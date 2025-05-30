package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.BloodPressureMapper;
import com.example.demo.model.dto.BloodPressureRecordDTO;
import com.example.demo.model.entity.BloodPressureRecord;
import com.example.demo.model.entity.User;
import com.example.demo.repository.BloodPressureRecordRepository;
import com.example.demo.repository.UserRepository;

@Service
public class BloodPressureRecordService {

	@Autowired
	private BloodPressureMapper bloodPressureMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BloodPressureRecordRepository bpRecordRepository;

	// 根據 accountId 取得使用者（User）物件
	public void saveRecord(BloodPressureRecordDTO dto) {
		User user = userRepository.findByAccount_Id(dto.getAccountId())
				.orElseThrow(() -> new RuntimeException("找不到使用者"));
		// DTO（資料傳輸物件）轉換成 Entity（實體物件）
		BloodPressureRecord record = new BloodPressureRecord();
		record.setUser(user);
		record.setSystolic(dto.getSystolic());
		record.setDiastolic(dto.getDiastolic());
		record.setNotes(dto.getNotes());
		record.setRecordDate(LocalDate.parse(dto.getRecordDate()));
		validateBloodPressure(record);
		bpRecordRepository.save(record);
	}

	public List<BloodPressureRecordDTO> getRecentRecords(Integer accountId) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("使用者不存在"));
		// 取得最近 5 筆資料，排序條件為建立時間由新到舊
		List<BloodPressureRecord> records = bpRecordRepository.findTop5ByUser_IdOrderByCreatedAtDesc(user.getId());

		return records.stream().map(r -> {
			BloodPressureRecordDTO dto = new BloodPressureRecordDTO();
			dto.setRecordId(r.getId());
			dto.setSystolic(r.getSystolic());
			dto.setDiastolic(r.getDiastolic());
			dto.setRecordDate(r.getRecordDate().toString());
			dto.setNotes(r.getNotes());
			dto.setAccountId(accountId);
			return dto;
		}).collect(Collectors.toList());
	}

	// 取得某帳號下的所有血壓紀錄，依紀錄時間降序排列
	public List<BloodPressureRecordDTO> getAllRecords(Integer accountId) {
		List<BloodPressureRecord> records = bpRecordRepository.findByUser_Account_IdOrderByRecordDateDesc(accountId);
		return bloodPressureMapper.toDtoList(records);
	}

	// 更新資料
	public void updateRecord(BloodPressureRecordDTO dto) {
		BloodPressureRecord record = bpRecordRepository.findById(dto.getRecordId())
				.orElseThrow(() -> new RuntimeException("紀錄不存在"));
		// 確認是否為此使用者的紀錄
		if (!record.getUser().getAccount().getId().equals(dto.getAccountId())) {
			throw new RuntimeException("無權修改此紀錄");
		}
		// 更新資料欄位
		record.setSystolic(dto.getSystolic());
		record.setDiastolic(dto.getDiastolic());
		record.setNotes(dto.getNotes());
		record.setRecordDate(LocalDate.parse(dto.getRecordDate()));
		validateBloodPressure(record);
		bpRecordRepository.save(record);
	}

	public void deleteRecord(Integer recordId, Integer accountId) {
		BloodPressureRecord record = bpRecordRepository.findById(recordId)
				.orElseThrow(() -> new RuntimeException("紀錄不存在"));

		if (!record.getUser().getAccount().getId().equals(accountId)) {
			throw new RuntimeException("無權刪除此紀錄");
		}

		bpRecordRepository.delete(record);
	}

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

}
