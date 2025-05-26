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

	public void saveRecord(BloodPressureRecordDTO dto) {
		User user = userRepository.findByAccount_Id(dto.getAccountId())
				.orElseThrow(() -> new RuntimeException("找不到使用者"));

		BloodPressureRecord record = new BloodPressureRecord();
		record.setUser(user);
		record.setSystolic(dto.getSystolic());
		record.setDiastolic(dto.getDiastolic());
		record.setNotes(dto.getNotes());
		record.setRecordDate(LocalDate.parse(dto.getRecordDate()));
		bpRecordRepository.save(record);
	}

	public List<BloodPressureRecordDTO> getRecentRecords(Integer accountId) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("使用者不存在"));

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

	public void updateRecord(BloodPressureRecordDTO dto) {
		BloodPressureRecord record = bpRecordRepository.findById(dto.getRecordId())
				.orElseThrow(() -> new RuntimeException("紀錄不存在"));

		// 確認是否為此使用者的紀錄
		if (!record.getUser().getAccount().getId().equals(dto.getAccountId())) {
			throw new RuntimeException("無權修改此紀錄");
		}

		record.setSystolic(dto.getSystolic());
		record.setDiastolic(dto.getDiastolic());
		record.setNotes(dto.getNotes());
		record.setRecordDate(LocalDate.parse(dto.getRecordDate()));

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

//	public List<BloodPressureRecordDTO> findByUserIdAndDateRange(Integer userId, LocalDate startDate,
//			LocalDate endDate) {
//		List<BloodPressureRecord> list = bpRecordRepository.findByUserIdAndDateRange(userId, startDate, endDate);
//		return list.stream().map(bloodPressureMapper::toDto).collect(Collectors.toList());
//	}
//
//	public List<BloodPressureRecordDTO> findRecentDaysByUserId(Integer userId, int days) {
//		LocalDate startDate = LocalDate.now().minusDays(days);
//		List<BloodPressureRecord> list = bpRecordRepository.findByUserIdInRecentDays(userId, startDate);
//		return list.stream().map(bloodPressureMapper::toDto).collect(Collectors.toList());
//	}

}
