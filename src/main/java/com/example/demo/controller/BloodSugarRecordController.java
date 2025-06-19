package com.example.demo.controller;

import java.util.List;
import java.util.Map;

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

// 血糖紀錄 API 控制器
@RestController
@RequestMapping("/rest/health/blood-sugar")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") // 跨來源請求
public class BloodSugarRecordController {

	@Autowired
	private BloodSugarService bloodSugarService;

	@Autowired
	private BloodSugarMapper bloodSugarMapper; // 轉換 DTO ↔ Entity 的工具

	@Autowired
	private UserService userService; // 取得目前登入使用者的資訊

	// [POST] 新增一筆血糖紀錄（會自動加入登入者帳號 ID）
	@PostMapping
	public ResponseEntity<String> addBloodSugar(@RequestBody BloodSugarRecordDTO dto) {
		System.out.println("收到的 DTO：" + dto);
		System.out.println("recordDate 是否有值？" + dto.getRecordDate());

		User currentUser = userService.getCurrentLoginUser(); // 取得目前登入者
		dto.setAccountId(currentUser.getAccount().getId()); // 設定帳號 ID
		bloodSugarService.save(dto); // 儲存資料（新增）
		return ResponseEntity.ok("新增成功！");
	}

	// [GET] 查詢目前使用者的所有血糖紀錄
	@GetMapping
	public ResponseEntity<List<BloodSugarRecordDTO>> findAll() {
		User currentUser = userService.getCurrentLoginUser(); // 根據登入者的帳號 ID 查詢資料，傳回 DTO 清單
		List<BloodSugarRecordDTO> dtoList = bloodSugarService.findByUserId(currentUser.getAccount().getId()); // 改用accountId查詢
		return ResponseEntity.ok(dtoList); // 回傳資料與 HTTP 200 成功狀態。
	}

	// [PUT] 更新指定 ID 的血糖紀錄
	@PutMapping("/{id}")
	public ResponseEntity<String> update(@PathVariable Integer id, @RequestBody BloodSugarRecordDTO dto) {
		// 先找出這筆原本的紀錄（用來做驗證或確認）
		BloodSugarRecordDTO originalDto = bloodSugarService.findById(id); // 回傳 DTO

		if (originalDto == null) {
			return ResponseEntity.notFound().build(); // 如果紀錄不存在，回傳 404（Not Found）
		}

		System.out.println("更新請求的 ID：" + id);
		System.out.println("查詢到的原始 DTO：" + originalDto);
		dto.setAccountId(originalDto.getAccountId()); // 保留原帳號 ID
		dto.setRecordId(id); // 設定紀錄 ID
		bloodSugarService.save(dto); // 儲存更新後資料
		return ResponseEntity.ok("更新成功！");
	}

	// [DELETE] 刪除指定 ID 的血糖紀錄
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		bloodSugarService.delete(id);
		return ResponseEntity.ok("刪除成功！"); // 呼叫 Service 刪除後，回傳成功訊息
	}

	// [GET] 查詢最新一筆血糖紀錄（用於「複製上一筆」功能）
	@GetMapping("/latest")
	public ResponseEntity<?> getLatestRecord() {
		User currentUser = userService.getCurrentLoginUser();
		BloodSugarRecordDTO latest = bloodSugarService.findLatestByUserId(currentUser.getAccount().getId());

		if (latest != null) {
			return ResponseEntity.ok(Map.of("data", latest)); // 包成 Map 給前端好處理
		} else {
			return ResponseEntity.ok(Map.of("data", null));
		}
	}
}
