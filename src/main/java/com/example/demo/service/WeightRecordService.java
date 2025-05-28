package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.WeightRecordDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.WeightRecord;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WeightRecordRepository;

@Service
public class WeightRecordService {

	@Autowired
	private WeightRecordRepository weightRecordRepository;

	@Autowired
	private UserRepository userRepository;

	public void saveRecord(WeightRecordDTO dto) {
		User user = userRepository.findByAccount_Id(dto.getAccountId())
				.orElseThrow(() -> new RuntimeException("使用者不存在"));

		WeightRecord record = new WeightRecord();
		record.setUser(user);
		record.setWeight(dto.getWeight());
		record.setHeight(dto.getHeight());
		record.setAge(dto.getAge());
		record.setBmi(dto.getBmi());
		record.setRecordDate(LocalDate.parse(dto.getRecordDate()));

		weightRecordRepository.save(record);
	}

	public List<WeightRecordDTO> getRecordsByAccountId(Integer accountId) {
		List<WeightRecord> records = weightRecordRepository.findByUser_Account_IdOrderByRecordDateAsc(accountId);
		return records.stream().map(r -> {
			WeightRecordDTO dto = new WeightRecordDTO();
			dto.setAccountId(accountId);
			dto.setWeight(r.getWeight());
			dto.setHeight(r.getHeight());
			dto.setAge(r.getAge());
			dto.setBmi(r.getBmi());
			dto.setRecordDate(r.getRecordDate().toString());
			return dto;
		}).collect(Collectors.toList());
	}

	public void updateRecord(WeightRecordDTO dto) {
		WeightRecord record = weightRecordRepository.findById(dto.getRecordId())
				.orElseThrow(() -> new RuntimeException("紀錄不存在"));

		if (!record.getUser().getAccountId().equals(dto.getAccountId())) {
			throw new RuntimeException("無權修改此紀錄");
		}

		record.setWeight(dto.getWeight());
		record.setHeight(dto.getHeight());
		record.setAge(dto.getAge());
		record.setBmi(dto.getBmi());
		record.setRecordDate(LocalDate.parse(dto.getRecordDate()));

		weightRecordRepository.save(record);
	}

	public void deleteRecord(Integer recordId, Integer accountId) {
		WeightRecord record = weightRecordRepository.findById(recordId)
				.orElseThrow(() -> new RuntimeException("紀錄不存在"));

		if (!record.getUser().getAccountId().equals(accountId)) {
			throw new RuntimeException("無權刪除此紀錄");
		}

		weightRecordRepository.delete(record);
	}

	public List<WeightRecordDTO> getRecent7RecordsByAccountId(Integer accountId) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("使用者不存在"));

		List<WeightRecord> records = weightRecordRepository.findTop5ByUser_IdOrderByCreatedAtDesc(user.getId());

		return records.stream().map(r -> {
			WeightRecordDTO dto = new WeightRecordDTO();
			dto.setRecordId(r.getId());
			dto.setAccountId(accountId);
			dto.setWeight(r.getWeight());
			dto.setHeight(r.getHeight());
			dto.setAge(r.getAge());
			dto.setBmi(r.getBmi());
			dto.setRecordDate(r.getRecordDate().toString());
			return dto;
		}).collect(Collectors.toList());
	}

	public WeightRecordDTO getLatestRecordByAccountId(Integer accountId) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("使用者不存在"));

		WeightRecord latest = weightRecordRepository.findTopByUser_IdOrderByCreatedAtDesc(user.getId());

		if (latest == null) {
			throw new RuntimeException("尚無體重紀錄");
		}

		WeightRecordDTO dto = new WeightRecordDTO();
		dto.setRecordId(latest.getId());
		dto.setAccountId(accountId);
		dto.setWeight(latest.getWeight());
		dto.setHeight(latest.getHeight());
		dto.setAge(latest.getAge());
		dto.setBmi(latest.getBmi());
		dto.setRecordDate(latest.getRecordDate().toString());

		return dto;
	}
}
