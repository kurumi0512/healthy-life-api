package com.example.demo.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.BloodPressureRecordDTO;
import com.example.demo.model.entity.BloodPressureRecord;

@Component // BloodPressureRecord ↔ BloodPressureRecordDTO 的轉換
public class BloodPressureMapper {

	@Autowired
	private ModelMapper modelMapper; // 自動將相同欄位名稱的物件進行轉換的工具

	// Entity → DTO（傳給前端）,後端查到的資料（Entity）轉成給前端看的格式（DTO）
	public BloodPressureRecordDTO toDto(BloodPressureRecord entity) {
		return modelMapper.map(entity, BloodPressureRecordDTO.class);
	}

	// DTO → Entity（前端送進來要存資料庫）
	public BloodPressureRecord toEntity(BloodPressureRecordDTO dto) {
		return modelMapper.map(dto, BloodPressureRecord.class);
	}

	// List<Entity> ➡ List<DTO>（轉換多筆資料）
	// 當你查詢出很多筆 BloodPressureRecord（例如近 7 筆資料），這個方法可以批次轉換成 DTO 給前端
	public List<BloodPressureRecordDTO> toDtoList(List<BloodPressureRecord> entityList) {
		return entityList.stream().map(this::toDto).collect(Collectors.toList());
	}

	// DTO ➡ Entity + 預設值處理（進階版）

	public BloodPressureRecord toEntityWithDefaults(BloodPressureRecordDTO dto) {
		BloodPressureRecord entity = modelMapper.map(dto, BloodPressureRecord.class);

		// 若前端未傳日期，預設今天
		if (dto.getRecordDate() == null) {
			entity.setRecordDate(LocalDate.now());
		} else {
			entity.setRecordDate(dto.getRecordDate());
		}
		return entity; // 👈 少了這行就會編譯錯誤
	}
}