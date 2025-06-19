package com.example.demo.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 註冊 ModelMapper 為 Spring 管理的元件（Bean）
// 可用於 DTO ↔ Entity 自動轉換

@Configuration
public class ModelMapperConfig {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setAmbiguityIgnored(true); // 忽略模糊對應錯誤
		return mapper;
	}
}
