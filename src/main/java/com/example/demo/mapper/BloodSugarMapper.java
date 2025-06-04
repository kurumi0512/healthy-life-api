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

	@PostConstruct
	public void setup() {
		// ⛔ 避免自動推斷錯誤，明確對應 DTO → Entity
		TypeMap<BloodSugarRecordDTO, BloodSugarRecord> toEntityMap = modelMapper
				.createTypeMap(BloodSugarRecordDTO.class, BloodSugarRecord.class);

		toEntityMap.addMappings(mapper -> {
			mapper.map(BloodSugarRecordDTO::getRecordId, BloodSugarRecord::setId);
			mapper.skip(BloodSugarRecord::setUser); // 讓 Service 設定關聯，不由 mapper 自動處理
		});

		// Entity → DTO（只需要明確 map id）
		TypeMap<BloodSugarRecord, BloodSugarRecordDTO> toDtoMap = modelMapper.createTypeMap(BloodSugarRecord.class,
				BloodSugarRecordDTO.class);

		toDtoMap.addMapping(BloodSugarRecord::getId, BloodSugarRecordDTO::setRecordId);
		// accountId 應由 service 額外補上，不由 mapper 推斷
	}

	public BloodSugarRecordDTO toDto(BloodSugarRecord entity) {
		BloodSugarRecordDTO dto = modelMapper.map(entity, BloodSugarRecordDTO.class);

		// ✨ 從 user 取出 accountId，補進 dto
		if (entity.getUser() != null && entity.getUser().getAccount() != null) {
			dto.setAccountId(entity.getUser().getAccount().getId());
		}

		return dto;
	}

	public BloodSugarRecord toEntity(BloodSugarRecordDTO dto) {
		return modelMapper.map(dto, BloodSugarRecord.class);
	}

	public BloodSugarRecord toEntityWithDefaults(BloodSugarRecordDTO dto) {
		BloodSugarRecord entity = toEntity(dto);
		if (dto.getRecordDate() == null) {
			entity.setRecordDate(LocalDate.now());
		}
		return entity;
	}
}