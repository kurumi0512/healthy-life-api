package com.example.demo.mapper;

import java.time.LocalDate;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.WeightRecordDTO;
import com.example.demo.model.entity.WeightRecord;

@Component // Spring Bean，讓 Spring Boot 自動託管它
public class WeightMapper {

	@Autowired
	private ModelMapper modelMapper;

	// Entity 轉 DTO（回傳給前端）
	// 避免前端知道 user、account 結構，只給 accountId
	public WeightRecordDTO toDto(WeightRecord entity) {
		WeightRecordDTO dto = new WeightRecordDTO();
		dto.setRecordId(entity.getId());
		dto.setAccountId(entity.getUser().getAccount().getId()); // ← 自動補上 Account ID
		dto.setWeight(entity.getWeight());
		dto.setHeight(entity.getHeight());
		dto.setAge(entity.getAge());
		dto.setBmi(entity.getBmi());
		if (entity.getRecordDate() != null) {
			dto.setRecordDate(entity.getRecordDate().toString()); // 避免前端知道 user、account 結構，只給 accountId
		}
		return dto;
	}

	// DTO 轉 Entity（儲存用）
	public WeightRecord toEntity(WeightRecordDTO dto) {
		WeightRecord entity = new WeightRecord();
		entity.setWeight(dto.getWeight());
		entity.setHeight(dto.getHeight());
		entity.setAge(dto.getAge());
		entity.setBmi(dto.getBmi());

		if (dto.getRecordDate() != null && !dto.getRecordDate().isBlank()) {
			entity.setRecordDate(LocalDate.parse(dto.getRecordDate()));
		} else {
			entity.setRecordDate(LocalDate.now()); // 若前端沒給日期，就用今天作為預設值
		}

		return entity;
	}

	// 可選擴充：Entity + User → DTO（更方便某些需要）
	// 這個方法的用途是確保即使上層忘了設 recordDate，也能補上今天的日期。它先用 toEntity()，然後補上日期。
	public WeightRecord toEntityWithDateDefault(WeightRecordDTO dto) {
		WeightRecord entity = toEntity(dto);
		if (entity.getRecordDate() == null) {
			entity.setRecordDate(LocalDate.now());
		}
		return entity;
	}
}
