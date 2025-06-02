package com.example.demo.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//是 Spring Boot 專案的啟動主程式（入口點）,整個系統的起點
@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}

//@Configuration                // 表示這是設定類別
//@EnableAutoConfiguration      // 自動載入 Spring Boot 的組態（像 Web、JPA、Security）
//@ComponentScan                // 自動掃描 @Component、@Service、@Controller 的類別
//這使得 Spring Boot 可以幫你自動建立 Spring 容器、載入設定、初始化 Bean 等。

//建立 Spring 容器
//
//掃描並註冊所有元件（Controller、Service、Repository）
//
//啟動內建的 Tomcat Server（預設使用 port 8080）
//
//讓你的 REST API 開始提供服務