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

	// 取得目前使用者的個人資料
	@GetMapping
	public ResponseEntity<UserProfileDto> getProfile(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId"); // 從 session 中取得登入者的 accountId

		return ResponseEntity.ok(userProfileService.getProfile(accountId));
		// 呼叫 userProfileService.getProfile(accountId)
		// 去資料庫查詢對應的個人資料
	}

	// 接收前端送來的資料並更新該使用者的個人檔案
	@PutMapping
	public ResponseEntity<?> updateProfile(@RequestBody UserProfileDto dto, HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		userProfileService.updateProfile(accountId, dto);
		return ResponseEntity.ok().build();
	}
}