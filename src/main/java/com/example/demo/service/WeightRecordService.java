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
		validateWeightRecord(dto);
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

	public void updateRecord(WeightRecordDTO dto) {
		validateWeightRecord(dto);
		WeightRecord record = weightRecordRepository.findById(dto.getRecordId())
				.orElseThrow(() -> new RuntimeException("紀錄不存在"));

		if (!record.getUser().getAccount().getId().equals(dto.getAccountId())) {
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

		// ✅ 正確驗證帳號
		if (!record.getUser().getAccount().getId().equals(accountId)) {
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

	private void validateWeightRecord(WeightRecordDTO dto) {
		if (dto.getHeight() < 50 || dto.getHeight() > 250) {
			throw new IllegalArgumentException("身高必須介於 50～250 公分");
		}
		if (dto.getWeight() < 10 || dto.getWeight() > 300) {
			throw new IllegalArgumentException("體重必須介於 10～300 公斤");
		}
		if (dto.getAge() < 1 || dto.getAge() > 120) {
			throw new IllegalArgumentException("年齡必須介於 1～120 歲");
		}
		if (dto.getBmi() < 10 || dto.getBmi() > 100) {
			throw new IllegalArgumentException("BMI 數值異常");
		}
	}
}
