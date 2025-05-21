package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.demo.repository")
@EntityScan(basePackages = "com.example.demo.model.entity")
@ComponentScan(basePackages = "com.example.demo")
@ServletComponentScan // 啟用 WebFilter 掃描, 因為 Springboot 預設會忽略 JavaWeb 基本的 @

public class HealthyLifeApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(HealthyLifeApiApplication.class, args);
	}
}
