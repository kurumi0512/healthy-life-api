package com.example.demo.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.mapper.BloodSugarMapper;
import com.example.demo.model.dto.BloodSugarRecordDTO;
import com.example.demo.model.entity.BloodSugarRecord;
import com.example.demo.model.entity.User;
import com.example.demo.service.AlertService;
import com.example.demo.service.BloodSugarService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/rest/health/blood-sugar")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")

public class BloodSugarRecordController {

	@Autowired
	private BloodSugarService bloodSugarService;

	@Autowired
	private BloodSugarMapper bloodSugarMapper; // ✅ 建議用 Mapper 類，而不是直接用 ModelMapper

	@Autowired
	private UserService userService; // ✅ 假設你有登入驗證，可以從這裡取得目前使用者

	@Autowired
	private AlertService alertService; // ➕ 注入推播服務

	// ➕ 新增血糖紀錄
	@PostMapping
	public ResponseEntity<String> addBloodSugar(@RequestBody BloodSugarRecordDTO dto, Principal principal) {
		BloodSugarRecord record = bloodSugarMapper.toEntity(dto);

		// ✅ 從登入狀態取得使用者（安全）
		User currentUser = userService.getCurrentLoginUser();
		record.setUser(currentUser);

		// ✅ 儲存紀錄
		bloodSugarService.save(record);

		// ✅ 推播邏輯：若餐後血糖異常，自動推播
		Double postMeal = dto.getPostMeal();
		if (postMeal != null) {
			Integer userId = currentUser.getId(); // ✅ 使用登入者 ID
			if (postMeal >= 200) {
				alertService.sendBloodSugarWarning(userId, "❗ 餐後血糖達糖尿病標準（≧200 mg/dL）");
			} else if (postMeal >= 140) {
				alertService.sendBloodSugarWarning(userId, "⚠️ 餐後血糖為糖尿病前期（140～199 mg/dL）");
			}
		}

		return ResponseEntity.ok("新增成功！");
	}

	// 🔍 查詢所有紀錄（可依使用者過濾）
	@GetMapping
	public ResponseEntity<List<BloodSugarRecordDTO>> findAll() {
		User currentUser = userService.getCurrentLoginUser(); // ✅ 只查自己紀錄
		List<BloodSugarRecord> records = bloodSugarService.findByUser(currentUser);

		List<BloodSugarRecordDTO> dtoList = records.stream().map(bloodSugarMapper::toDto).collect(Collectors.toList());

		return ResponseEntity.ok(dtoList);
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> update(@PathVariable Integer id, @RequestBody BloodSugarRecordDTO dto) {
		BloodSugarRecord record = bloodSugarService.findById(id);
		if (record == null) {
			return ResponseEntity.notFound().build();
		}
		record.setFasting(dto.getFasting());
		record.setPostMeal(dto.getPostMeal());
		record.setRecordDate(dto.getRecordDate());
		record.setNotes(dto.getNotes());

		bloodSugarService.save(record);
		return ResponseEntity.ok("更新成功！");
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		bloodSugarService.delete(id);
		return ResponseEntity.ok("刪除成功！");
	}

}
