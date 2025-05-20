package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class TestController {

	@GetMapping("/ping")
	public String ping() {
		return "pong";
	}
}