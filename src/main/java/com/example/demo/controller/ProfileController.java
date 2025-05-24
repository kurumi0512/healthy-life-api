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
import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/rest/profile")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ProfileController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccountRepository accountRepository;

	// ✅ 查詢個人資料
	@GetMapping
	public ResponseEntity<UserProfileDto> getProfile(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");

		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("找不到個人資料"));
		Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("找不到帳號資料"));

		UserProfileDto dto = new UserProfileDto();
		dto.setName(user.getName());
		dto.setAge(user.getAge());
		dto.setGender(user.getGender());
		dto.setGoal(user.getGoal());
		dto.setTargetWeight(user.getTargetWeight());
		dto.setAgeGroup(user.getAgeGroup());
		dto.setEmail(account.getEmail());

		return ResponseEntity.ok(dto);
	}

	// ✅ 更新個人資料
	@PutMapping
	public ResponseEntity<?> updateProfile(@RequestBody UserProfileDto dto, HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");

		User user = userRepository.findByAccount_Id(accountId).orElseThrow(() -> new RuntimeException("找不到個人資料"));

		user.setName(dto.getName());
		user.setAge(dto.getAge());
		user.setGender(dto.getGender());
		user.setGoal(dto.getGoal());
		user.setTargetWeight(dto.getTargetWeight());
		user.setAgeGroup(dto.getAgeGroup());

		userRepository.save(user);
		return ResponseEntity.ok().build();
	}
}