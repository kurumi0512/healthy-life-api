package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${frontend.url}")
	private String frontendUrl;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 所有 API 都支援跨域
				.allowedOrigins(frontendUrl) // 前端網址
				.allowedMethods("GET", "POST", "PUT", "DELETE") // 允許的方法
				.allowedHeaders("*") // 所有標頭都允許
				.allowCredentials(true); // 允許 cookie、session 等資訊
	}
}
