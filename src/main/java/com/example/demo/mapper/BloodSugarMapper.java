package com.example.demo.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.BloodSugarDTO;
import com.example.demo.model.entity.BloodSugarRecord;

@Component // 交給 Spring 管理
public class BloodSugarMapper {

	@Autowired
	private ModelMapper modelMapper;

	// Entity 轉 DTO（回傳給前端）
	public BloodSugarDTO toDto(BloodSugarRecord entity) {
		return modelMapper.map(entity, BloodSugarDTO.class);
	}

	// DTO 轉 Entity（接收前端資料轉成資料表格式）
	public BloodSugarRecord toEntity(BloodSugarDTO dto) {
		return modelMapper.map(dto, BloodSugarRecord.class);
	}
}