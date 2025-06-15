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
	private BloodPressureMapper bloodPressureMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BloodPressureRecordRepository bpRecordRepository;

	// 根據 accountId 取得使用者（User）物件
	@Override
	@CheckNotes
	public void saveRecord(BloodPressureRecordDTO dto) {
		User user = userRepository.findByAccount_Id(dto.getAccountId())
				.orElseThrow(() -> new RuntimeException("找不到使用者"));

		// 改呼叫你新增的 toEntityWithDefaults 方法
		BloodPressureRecord record = bloodPressureMapper.toEntityWithDefaults(dto);
		record.setUser(user); // 補上關聯（這還是要手動）

		validateBloodPressure(record);
		bpRecordRepository.save(record);
		System.out.println("📝 使用者選擇的日期：" + dto.getRecordDate());
	}

	@Override
	public List<BloodPressureRecordDTO> getRecentRecords(Integer accountId) {
		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("使用者不存在"));

		List<BloodPressureRecord> records = bpRecordRepository.findTop5ByUser_IdOrderByCreatedAtDesc(user.getId());

		// 用 mapper 做轉換
		List<BloodPressureRecordDTO> dtoList = bloodPressureMapper.toDtoList(records);
		// 額外補上 accountId（ModelMapper 不會自動做這件事）
		dtoList.forEach(dto -> dto.setAccountId(accountId));
		return dtoList;
	}

	// 取得某帳號下的所有血壓紀錄，依紀錄時間降序排列
	@Override
	public List<BloodPressureRecordDTO> getAllRecords(Integer accountId) {
		List<BloodPressureRecord> records = bpRecordRepository.findByUser_Account_IdOrderByRecordDateDesc(accountId);
		return bloodPressureMapper.toDtoList(records);
	}

	// 更新資料
	@Override
	@CheckNotes
	public void updateRecord(BloodPressureRecordDTO dto) {
		BloodPressureRecord record = bpRecordRepository.findById(dto.getRecordId())
				.orElseThrow(() -> new RuntimeException("紀錄不存在"));

		if (!record.getUser().getAccount().getId().equals(dto.getAccountId())) {
			throw new RuntimeException("無權修改此紀錄");
		}

		record.setSystolic(dto.getSystolic());
		record.setDiastolic(dto.getDiastolic());
		record.setNotes(dto.getNotes());

		// ✅ 只有使用者有選日期時才更新
		if (dto.getRecordDate() != null) {
			record.setRecordDate(dto.getRecordDate());
		}

		validateBloodPressure(record);
		bpRecordRepository.save(record);
	}

	@Override
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

	@Override
	public BloodPressureRecordDTO findLatestByUserId(Integer accountId) {
		BloodPressureRecord latest = bpRecordRepository.findFirstByUser_Account_IdOrderByRecordDateDesc(accountId);
		return (latest != null) ? bloodPressureMapper.toDto(latest) : null;
	}

}
