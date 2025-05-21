package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.mapper.BloodSugarMapper;
import com.example.demo.model.dto.BloodSugarDTO;
import com.example.demo.model.entity.BloodSugarRecord;
import com.example.demo.model.entity.User;
import com.example.demo.service.BloodSugarService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/rest/health/blood-sugar")
public class BloodSugarController {

	@Autowired
	private BloodSugarService bloodSugarService;

	@Autowired
	private BloodSugarMapper bloodSugarMapper; // ✅ 建議用 Mapper 類，而不是直接用 ModelMapper

	@Autowired
	private UserService userService; // ✅ 假設你有登入驗證，可以從這裡取得目前使用者

	// ➕ 新增血糖紀錄
	@PostMapping
	public ResponseEntity<String> addBloodSugar(@RequestBody BloodSugarDTO dto) {
		BloodSugarRecord record = bloodSugarMapper.toEntity(dto);

		// ✅ 從登入狀態取得目前 user（不要從前端傳 userId）
		User currentUser = userService.getCurrentLoginUser(); // 假設你有這樣的方法
		record.setUser(currentUser);

		bloodSugarService.save(record);
		return ResponseEntity.ok("新增成功！");
	}

	// 🔍 查詢所有紀錄（可依使用者過濾）
	@GetMapping
	public ResponseEntity<List<BloodSugarDTO>> findAll() {
		User currentUser = userService.getCurrentLoginUser(); // ✅ 只查自己紀錄
		List<BloodSugarRecord> records = bloodSugarService.findByUser(currentUser);

		List<BloodSugarDTO> dtoList = records.stream().map(bloodSugarMapper::toDto).collect(Collectors.toList());

		return ResponseEntity.ok(dtoList);
	}
}
