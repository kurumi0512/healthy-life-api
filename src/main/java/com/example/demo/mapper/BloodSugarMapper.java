package com.example.demo.mapper;

import java.time.LocalDate;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.BloodSugarRecordDTO;
import com.example.demo.model.entity.BloodSugarRecord;

@Component // 交給 Spring 管理
public class BloodSugarMapper {

	@Autowired
	private ModelMapper modelMapper;

	// Entity 轉 DTO（回傳給前端）
	public BloodSugarRecordDTO toDto(BloodSugarRecord entity) {
		return modelMapper.map(entity, BloodSugarRecordDTO.class);
	}

	// DTO 轉 Entity（接收前端資料轉成資料表格式）
	public BloodSugarRecord toEntity(BloodSugarRecordDTO dto) {
		return modelMapper.map(dto, BloodSugarRecord.class);
	}

	public BloodSugarRecord toEntityWithDefaults(BloodSugarRecordDTO dto) {
		BloodSugarRecord entity = modelMapper.map(dto, BloodSugarRecord.class);
		if (dto.getRecordDate() == null) {
			entity.setRecordDate(LocalDate.now());
		}
		return entity;
	}
}