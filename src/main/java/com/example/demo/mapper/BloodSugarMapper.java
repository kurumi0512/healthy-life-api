package com.example.demo.mapper;

import java.time.LocalDate;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.BloodSugarRecordDTO;
import com.example.demo.model.entity.BloodSugarRecord;

import jakarta.annotation.PostConstruct;

@Component
public class BloodSugarMapper {

	@Autowired
	private ModelMapper modelMapper;

	// 初始化時，手動設定 DTO ↔ Entity 的對應規則，避免自動對應錯誤
	@PostConstruct
	public void setup() {
		// DTO ➝ Entity：只對應必要欄位，避免錯誤或不應該處理的欄位（像 user）被自動轉換
		TypeMap<BloodSugarRecordDTO, BloodSugarRecord> toEntityMap = modelMapper
				.createTypeMap(BloodSugarRecordDTO.class, BloodSugarRecord.class);

		toEntityMap.addMappings(mapper -> {
			mapper.map(BloodSugarRecordDTO::getRecordId, BloodSugarRecord::setId);
			mapper.skip(BloodSugarRecord::setUser); // 不處理 user，交由 Service 層補上
		});

		// Entity ➝ DTO：補上 recordId 對應
		TypeMap<BloodSugarRecord, BloodSugarRecordDTO> toDtoMap = modelMapper.createTypeMap(BloodSugarRecord.class,
				BloodSugarRecordDTO.class);

		toDtoMap.addMapping(BloodSugarRecord::getId, BloodSugarRecordDTO::setRecordId);
		// accountId 不由 mapper 推斷，會在後面手動補上
	}

	// Entity ➝ DTO（回傳前端用），並手動補上 accountId
	public BloodSugarRecordDTO toDto(BloodSugarRecord entity) {
		BloodSugarRecordDTO dto = modelMapper.map(entity, BloodSugarRecordDTO.class);

		// 從 user → account → id 取得 accountId 給前端
		if (entity.getUser() != null && entity.getUser().getAccount() != null) {
			dto.setAccountId(entity.getUser().getAccount().getId());
		}

		return dto;
	}

	// DTO ➝ Entity（用 ModelMapper 自動對應）
	public BloodSugarRecord toEntity(BloodSugarRecordDTO dto) {
		return modelMapper.map(dto, BloodSugarRecord.class);
	}

	// 進階轉換：若沒有輸入日期，自動補上今天日期
	public BloodSugarRecord toEntityWithDefaults(BloodSugarRecordDTO dto) {
		BloodSugarRecord entity = toEntity(dto);
		if (dto.getRecordDate() == null) {
			entity.setRecordDate(LocalDate.now());
		}
		return entity;
	}
}