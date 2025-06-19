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

	// [GET] 取得目前使用者的個人資料
	@GetMapping
	public ResponseEntity<UserProfileDto> getProfile(HttpSession session) {
		// 從 session 中取得目前登入者的帳號 ID（accountId）
		Integer accountId = (Integer) session.getAttribute("accountId"); // 從 session 中取得登入者的 accountId

		// 呼叫 service 查詢並回傳該使用者的個人資料（DTO 格式）
		return ResponseEntity.ok(userProfileService.getProfile(accountId));
	}

	// [PUT] 更新使用者個人資料（接收前端送來的 JSON 資料）
	@PutMapping
	public ResponseEntity<?> updateProfile(@RequestBody UserProfileDto dto, HttpSession session) {
		// 從 session 中取得目前登入者的帳號 ID
		Integer accountId = (Integer) session.getAttribute("accountId");

		// 呼叫 service 更新該使用者的資料
		userProfileService.updateProfile(accountId, dto);

		// 回傳空成功回應（狀態碼 200 OK）
		return ResponseEntity.ok().build();
	}
}