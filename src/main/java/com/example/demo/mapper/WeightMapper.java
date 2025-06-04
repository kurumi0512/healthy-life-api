package com.example.demo.mapper;

import java.time.LocalDate;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.WeightRecordDTO;
import com.example.demo.model.entity.WeightRecord;

@Component // Spring Bean，讓 Spring Boot 自動託管它
//mapper 的功能是「資料轉換」：它負責在 DTO（資料傳輸物件） 和 Entity（資料庫實體物件） 之間轉換
public class WeightMapper {

	@Autowired
	private ModelMapper modelMapper;

	// Entity 轉 DTO（回傳給前端）
	// 避免前端知道 user、account 結構，只給 accountId
	public WeightRecordDTO toDto(WeightRecord entity) {
		WeightRecordDTO dto = new WeightRecordDTO();
		dto.setRecordId(entity.getId());
		dto.setAccountId(entity.getUser().getAccount().getId()); // 避免前端知道 User/Account 結構
		dto.setWeight(entity.getWeight());
		dto.setHeight(entity.getHeight());
		dto.setAge(entity.getAge());
		dto.setBmi(entity.getBmi());
		if (entity.getRecordDate() != null) {
			dto.setRecordDate(entity.getRecordDate());
		}
		return dto;
	}

	// DTO 轉 Entity（儲存用）
	public WeightRecord toEntity(WeightRecordDTO dto) {
		WeightRecord entity = new WeightRecord();
		entity.setWeight(dto.getWeight());
		entity.setHeight(dto.getHeight());
		entity.setAge(dto.getAge());
		entity.setBmi(dto.getBmi());

		// 直接設 LocalDate
		if (dto.getRecordDate() != null) {
			entity.setRecordDate(dto.getRecordDate());
		} else {
			entity.setRecordDate(LocalDate.now());
		}

		return entity;
	}

	// 可選擴充：Entity + User → DTO（更方便某些需要）
	// 萬一 recordDate 是空的，會自動補上今天的日期
	public WeightRecord toEntityWithDateDefault(WeightRecordDTO dto) {
		WeightRecord entity = toEntity(dto);
		if (entity.getRecordDate() == null) {
			entity.setRecordDate(LocalDate.now());
		}
		return entity;
	}
}
