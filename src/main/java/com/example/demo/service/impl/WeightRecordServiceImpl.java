package com.example.demo.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.WeightMapper;
import com.example.demo.model.dto.WeightRecordDTO;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.WeightRecord;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WeightRecordRepository;
import com.example.demo.service.WeightRecordService;

@Service
public class WeightRecordServiceImpl implements WeightRecordService {

	@Autowired
	private WeightRecordRepository weightRecordRepository; // 存取體重紀錄資料（CRUD 操作）

	@Autowired
	private UserRepository userRepository; // 查找使用者（用來確認紀錄屬於誰）

	@Autowired
	private WeightMapper weightMapper; // 負責 Entity ↔ DTO 的轉換

	// 儲存一筆體重紀錄（新增）
	@Override
	public void saveRecord(WeightRecordDTO dto) {
		validateWeightRecord(dto); // 驗證欄位數值是否合理

		// 若使用者未指定記錄日期，預設為今天
		if (dto.getRecordDate() == null) {
			dto.setRecordDate(LocalDate.now());
		}

		// 根據帳號 ID 取得對應使用者
		User user = userRepository.findByAccount_Id(dto.getAccountId())
				.orElseThrow(() -> new RuntimeException("使用者不存在"));

		// 將 DTO 轉為 Entity 並設定對應的 User
		WeightRecord record = weightMapper.toEntity(dto);
		record.setUser(user);

		// 儲存資料
		weightRecordRepository.save(record);
	}

	// 查詢某帳號的所有體重紀錄（依記錄日期由新到舊排序）
	@Override
	public List<WeightRecordDTO> getRecordsByAccountId(Integer accountId) {
		List<WeightRecord> records = weightRecordRepository.findByUser_Account_IdOrderByRecordDateDesc(accountId);
		return records.stream().map(weightMapper::toDto).collect(Collectors.toList());
	}

	// 更新指定紀錄內容
	@Override
	public void updateRecord(WeightRecordDTO dto) {
		validateWeightRecord(dto); // 驗證欄位

		// 找出原始紀錄
		WeightRecord record = weightRecordRepository.findById(dto.getRecordId())
				.orElseThrow(() -> new RuntimeException("紀錄不存在"));

		// 驗證紀錄是否屬於該帳號（避免越權修改）
		if (!record.getUser().getAccount().getId().equals(dto.getAccountId())) {
			throw new RuntimeException("無權修改此紀錄");
		}

		// 更新欄位
		record.setWeight(dto.getWeight());
		record.setHeight(dto.getHeight());
		record.setAge(dto.getAge());
		record.setBmi(dto.getBmi());
		record.setRecordDate(dto.getRecordDate() != null ? dto.getRecordDate() : LocalDate.now());

		weightRecordRepository.save(record);
	}

	// 刪除指定紀錄（需驗證是否屬於該帳號）
	@Override
	public void deleteRecord(Integer recordId, Integer accountId) {
		WeightRecord record = weightRecordRepository.findById(recordId)
				.orElseThrow(() -> new RuntimeException("紀錄不存在"));

		if (!record.getUser().getAccount().getId().equals(accountId)) {
			throw new RuntimeException("無權刪除此紀錄");
		}

		weightRecordRepository.delete(record);
	}

	// 查詢最近 5 筆體重紀錄（依建立時間排序）
	@Override
	public List<WeightRecordDTO> getRecent5RecordsByAccountId(Integer accountId) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("使用者不存在"));

		List<WeightRecord> records = weightRecordRepository.findTop5ByUser_IdOrderByCreatedAtDesc(user.getId());

		return records.stream().map(weightMapper::toDto).collect(Collectors.toList());
	}

	// 查詢最新一筆體重紀錄（供 AI 建議功能預設填入使用）
	@Override
	public Optional<WeightRecordDTO> getLatestRecordByAccountId(Integer accountId) {
		return userRepository.findByAccount_Id(accountId).flatMap(user -> {
			WeightRecord latest = weightRecordRepository.findTopByUser_IdOrderByCreatedAtDesc(user.getId());
			return Optional.ofNullable(latest).map(weightMapper::toDto);
		});
	}

	// 自訂欄位驗證方法，避免不合理輸入
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