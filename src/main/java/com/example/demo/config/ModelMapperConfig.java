package com.example.demo.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Springboot 啟動完成前會先執行此配置
// 註冊 ModelMapper 這個物件成為 Spring 管理的 Bean

@Configuration
public class ModelMapperConfig {

	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}
}

//Springboot 會自動建立此物件並管理
//其他程式可以透過 @Autowired 來取得該實體物件
//ModelMapper 是一個 Java 的物件映射工具，主要用途是：
//DTO ↔ Entity 自動轉換	將資料傳輸物件（DTO）轉換成資料實體（Entity），反之亦然
//自動對應相同屬性名稱	不需手動設定 getXxx() / setXxx()
//支援深層嵌套轉換 也可以對巢狀物件進行轉換（如 user.getAccount().getUsername()）