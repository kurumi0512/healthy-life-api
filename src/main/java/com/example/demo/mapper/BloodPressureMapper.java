package com.example.demo.mapper;

import java.util.List;
import java.util.stream.Collectors;

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

	// Entity List → DTO List
	public List<BloodPressureRecordDTO> toDtoList(List<BloodPressureRecord> entityList) {
		return entityList.stream().map(this::toDto).collect(Collectors.toList());
	}
}