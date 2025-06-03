package com.example.demo.mapper;

import java.time.LocalDate;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.WeightRecordDTO;
import com.example.demo.model.entity.WeightRecord;

@Component
public class WeightMapper {

	@Autowired
	private ModelMapper modelMapper;

	// Entity 轉 DTO（回傳給前端）
	public WeightRecordDTO toDto(WeightRecord entity) {
		WeightRecordDTO dto = new WeightRecordDTO();
		dto.setRecordId(entity.getId());
		dto.setAccountId(entity.getUser().getAccount().getId()); // ← 自動補上 Account ID
		dto.setWeight(entity.getWeight());
		dto.setHeight(entity.getHeight());
		dto.setAge(entity.getAge());
		dto.setBmi(entity.getBmi());
		if (entity.getRecordDate() != null) {
			dto.setRecordDate(entity.getRecordDate().toString());
		}
		return dto;
	}

	// DTO 轉 Entity（不含 User 關聯，Service 要補）
	public WeightRecord toEntity(WeightRecordDTO dto) {
		WeightRecord entity = new WeightRecord();
		entity.setWeight(dto.getWeight());
		entity.setHeight(dto.getHeight());
		entity.setAge(dto.getAge());
		entity.setBmi(dto.getBmi());

		if (dto.getRecordDate() != null && !dto.getRecordDate().isBlank()) {
			entity.setRecordDate(LocalDate.parse(dto.getRecordDate()));
		} else {
			entity.setRecordDate(LocalDate.now()); // 預設今天
		}

		return entity;
	}

	// 可選擴充：Entity + User → DTO（更方便某些需要）
	public WeightRecord toEntityWithDateDefault(WeightRecordDTO dto) {
		WeightRecord entity = toEntity(dto);
		if (entity.getRecordDate() == null) {
			entity.setRecordDate(LocalDate.now());
		}
		return entity;
	}
}
