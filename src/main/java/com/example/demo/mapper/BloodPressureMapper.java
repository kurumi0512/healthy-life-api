package com.example.demo.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.BloodPressureRecordDTO;
import com.example.demo.model.entity.BloodPressureRecord;

@Component
public class BloodPressureMapper {

	@Autowired
	private ModelMapper modelMapper;

	// Entity → DTO（傳給前端）
	public BloodPressureRecordDTO toDto(BloodPressureRecord entity) {
		return modelMapper.map(entity, BloodPressureRecordDTO.class);
	}

	// DTO → Entity（接收前端輸入）
	public BloodPressureRecord toEntity(BloodPressureRecordDTO dto) {
		return modelMapper.map(dto, BloodPressureRecord.class);
	}
}