package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.UserProfileDto;
import com.example.demo.service.UserProfileService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/rest/profile")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ProfileController {

	@Autowired
	private UserProfileService userProfileService;

	@GetMapping
	public ResponseEntity<UserProfileDto> getProfile(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		return ResponseEntity.ok(userProfileService.getProfile(accountId));
	}

	@PutMapping
	public ResponseEntity<?> updateProfile(@RequestBody UserProfileDto dto, HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		userProfileService.updateProfile(accountId, dto);
		return ResponseEntity.ok().build();
	}
}