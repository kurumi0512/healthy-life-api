package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.WeightMapper;
import com.example.demo.model.dto.WeightRecordDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.WeightRecord;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WeightRecordRepository;

@Service
public class WeightRecordService {

	@Autowired
	private WeightRecordRepository weightRecordRepository; // 存取體重紀錄資料（CRUD 操作）

	@Autowired
	private UserRepository userRepository; // 查找使用者（用來確認紀錄屬於誰）

	@Autowired
	private WeightMapper weightMapper; // 負責 Entity 與 DTO 的轉換

	public void saveRecord(WeightRecordDTO dto) {
		validateWeightRecord(dto); // 呼叫 validateWeightRecord 驗證欄位正確性

		// 預設記錄日期為今天
		if (dto.getRecordDate() == null) {
			dto.setRecordDate(LocalDate.now());
		}

		User user = userRepository.findByAccount_Id(dto.getAccountId())
				.orElseThrow(() -> new RuntimeException("使用者不存在")); // 根據 dto.getAccountId() 查出對應的 User。

		WeightRecord record = weightMapper.toEntity(dto); // 把 DTO 轉成 Entity，並補上 user 物件。
		record.setUser(user); // 補上關聯

		weightRecordRepository.save(record);
	}

	// 取得某帳號所有體重紀錄（依紀錄日排序）
	public List<WeightRecordDTO> getRecordsByAccountId(Integer accountId) {
		// 用帳號 ID 找出該帳號的所有體重紀錄（由新到舊排序）。
		// 使用 Mapper 將紀錄轉成 DTO 回傳前端。
		List<WeightRecord> records = weightRecordRepository.findByUser_Account_IdOrderByRecordDateDesc(accountId);
		return records.stream().map(weightMapper::toDto).collect(Collectors.toList());
	}

	// 更新指定紀錄
	public void updateRecord(WeightRecordDTO dto) {
		validateWeightRecord(dto);
		WeightRecord record = weightRecordRepository.findById(dto.getRecordId())
				.orElseThrow(() -> new RuntimeException("紀錄不存在"));

		if (!record.getUser().getAccount().getId().equals(dto.getAccountId())) {
			throw new RuntimeException("無權修改此紀錄");
		}

		// 更新欄位
		record.setWeight(dto.getWeight());
		record.setHeight(dto.getHeight());
		record.setAge(dto.getAge());
		record.setBmi(dto.getBmi());
		if (dto.getRecordDate() != null) {
			record.setRecordDate(dto.getRecordDate());
		} else {
			record.setRecordDate(LocalDate.now());
		}

		weightRecordRepository.save(record);
	}

	// 刪除體重紀錄（要驗證是否為本人紀錄）
	public void deleteRecord(Integer recordId, Integer accountId) {
		WeightRecord record = weightRecordRepository.findById(recordId)
				.orElseThrow(() -> new RuntimeException("紀錄不存在"));

		if (!record.getUser().getAccount().getId().equals(accountId)) {
			throw new RuntimeException("無權刪除此紀錄");
		}

		weightRecordRepository.delete(record);
	}

	// 取得最近 5 筆紀錄（按建立時間排序）
	public List<WeightRecordDTO> getRecent5RecordsByAccountId(Integer accountId) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("使用者不存在"));

		List<WeightRecord> records = weightRecordRepository.findTop5ByUser_IdOrderByCreatedAtDesc(user.getId());

		return records.stream().map(weightMapper::toDto).collect(Collectors.toList());
	}

	// 取得最新一筆紀錄(要串在AI上但還沒用)
	public WeightRecordDTO getLatestRecordByAccountId(Integer accountId) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("使用者不存在"));

		WeightRecord latest = weightRecordRepository.findTopByUser_IdOrderByCreatedAtDesc(user.getId());

		if (latest == null) {
			throw new RuntimeException("尚無體重紀錄");
		}

		return weightMapper.toDto(latest);
	}

	// 自訂欄位驗證方法
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