package com.example.demo.mapper;

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
	private ModelMapper modelMapper; // 自動對應欄位名稱相同的屬性，簡化轉換邏輯

	// Entity → DTO：後端查資料後轉換成給前端使用的格式
	public BloodPressureRecordDTO toDto(BloodPressureRecord entity) {
		return modelMapper.map(entity, BloodPressureRecordDTO.class);
	}

	// DTO → Entity：前端送資料過來要存資料庫時轉換成 Entity
	public BloodPressureRecord toEntity(BloodPressureRecordDTO dto) {
		return modelMapper.map(dto, BloodPressureRecord.class);
	}

	// 多筆 Entity → 多筆 DTO（通常查詢列表資料時會用到）
	public List<BloodPressureRecordDTO> toDtoList(List<BloodPressureRecord> entityList) {
		return entityList.stream().map(this::toDto).collect(Collectors.toList());
	}

	// 進階轉換：DTO → Entity 並處理預設值（例如只在日期不為 null 時才設定）
	public BloodPressureRecord toEntityWithDefaults(BloodPressureRecordDTO dto) {
		BloodPressureRecord entity = modelMapper.map(dto, BloodPressureRecord.class);

		// 若DTO 有傳入日期，就設定到 Entity（避免預設值被覆蓋）
		if (dto.getRecordDate() != null) {
			entity.setRecordDate(dto.getRecordDate());
		}

		return entity;
	}
}