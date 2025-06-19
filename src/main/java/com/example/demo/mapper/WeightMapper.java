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

	// Entity ➜ DTO：用來回傳給前端顯示
	public WeightRecordDTO toDto(WeightRecord entity) {
		WeightRecordDTO dto = new WeightRecordDTO();
		dto.setRecordId(entity.getId());

		// 只傳回 accountId，隱藏後端的 User 結構
		dto.setAccountId(entity.getUser().getAccount().getId());
		dto.setWeight(entity.getWeight());
		dto.setHeight(entity.getHeight());
		dto.setAge(entity.getAge());
		dto.setBmi(entity.getBmi());

		// 避免 NullPointer
		if (entity.getRecordDate() != null) {
			dto.setRecordDate(entity.getRecordDate());
		}
		return dto;
	}

	// DTO ➜ Entity：用來存進資料庫
	public WeightRecord toEntity(WeightRecordDTO dto) {
		WeightRecord entity = new WeightRecord();
		entity.setWeight(dto.getWeight());
		entity.setHeight(dto.getHeight());
		entity.setAge(dto.getAge());
		entity.setBmi(dto.getBmi());

		// 沒有日期就補上今天
		if (dto.getRecordDate() != null) {
			entity.setRecordDate(dto.getRecordDate());
		} else {
			entity.setRecordDate(LocalDate.now());
		}

		return entity;
	}

	// 如果已有 toEntity，但想保證一定有日期，可呼叫這個方法
	public WeightRecord toEntityWithDateDefault(WeightRecordDTO dto) {
		WeightRecord entity = toEntity(dto);
		if (entity.getRecordDate() == null) {
			entity.setRecordDate(LocalDate.now());
		}
		return entity;
	}
}
