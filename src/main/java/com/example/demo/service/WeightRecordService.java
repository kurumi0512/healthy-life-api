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
		User user = userRepository.findByAccountId(dto.getAccountId())
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
		List<WeightRecord> records = weightRecordRepository.findByUser_AccountIdOrderByRecordDateAsc(accountId);
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
}
