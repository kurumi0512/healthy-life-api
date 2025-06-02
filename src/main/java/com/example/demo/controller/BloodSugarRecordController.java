package com.example.demo.controller;

import java.util.List;

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
import com.example.demo.model.entity.User;
import com.example.demo.service.BloodSugarService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/rest/health/blood-sugar")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class BloodSugarRecordController {

	@Autowired
	private BloodSugarService bloodSugarService;

	@Autowired
	private BloodSugarMapper bloodSugarMapper;

	@Autowired
	private UserService userService;

	// ➕ 新增血糖紀錄
	@PostMapping
	public ResponseEntity<String> addBloodSugar(@RequestBody BloodSugarRecordDTO dto) {
		// 從目前登入者取得 user 對應的 accountId（血糖 DTO 需要）
		User currentUser = userService.getCurrentLoginUser();
		dto.setAccountId(currentUser.getAccount().getId());

		bloodSugarService.save(dto); // ✅ 傳 DTO，不是 Entity
		return ResponseEntity.ok("新增成功！");
	}

	// 🔍 查詢所有紀錄
	@GetMapping
	public ResponseEntity<List<BloodSugarRecordDTO>> findAll() {
		User currentUser = userService.getCurrentLoginUser();
		List<BloodSugarRecordDTO> dtoList = bloodSugarService.findByUserId(currentUser.getAccount().getId()); // ✅ 改用
																												// accountId
																												// 查詢
		return ResponseEntity.ok(dtoList);
	}

	// ✏️ 更新血糖紀錄
	@PutMapping("/{id}")
	public ResponseEntity<String> update(@PathVariable Integer id, @RequestBody BloodSugarRecordDTO dto) {
		BloodSugarRecordDTO originalDto = bloodSugarService.findById(id); // ✅ 回傳 DTO

		if (originalDto == null) {
			return ResponseEntity.notFound().build();
		}

		// ⚠️ 這裡你可以限制只能更新自己帳號的紀錄（驗證 accountId）

		dto.setAccountId(originalDto.getAccountId()); // 維持原紀錄所屬帳號
		dto.setRecordId(id); // 確保要更新的是這筆
		bloodSugarService.save(dto); // ✅ 更新使用相同 save 方法（由 id 區分新增或更新）
		return ResponseEntity.ok("更新成功！");
	}

	// ❌ 刪除紀錄
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		bloodSugarService.delete(id);
		return ResponseEntity.ok("刪除成功！");
	}
}
