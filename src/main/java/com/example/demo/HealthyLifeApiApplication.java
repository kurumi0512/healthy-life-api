package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication // 是 Spring Boot 專案的啟動主程式（入口點）,整個系統的起點
@EnableJpaRepositories(basePackages = "com.example.demo.repository")
@EntityScan(basePackages = "com.example.demo.model.entity")
@ComponentScan(basePackages = "com.example.demo") // 自動掃描 @Component、@Service、@Controller 的類別
@ServletComponentScan // 啟用 WebFilter 掃描, 因為 Springboot 預設會忽略 JavaWeb 基本的 @
@EnableJpaAuditing

public class HealthyLifeApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(HealthyLifeApiApplication.class, args);
	}

}
