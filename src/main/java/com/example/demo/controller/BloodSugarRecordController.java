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

//處理血糖紀錄相關的請求
@RestController // REST API 控制器，回傳的資料會自動轉成 JSON
@RequestMapping("/rest/health/blood-sugar") // API 路徑前綴
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") // 跨來源請求
public class BloodSugarRecordController {

	@Autowired
	private BloodSugarService bloodSugarService;

	@Autowired
	private BloodSugarMapper bloodSugarMapper; // 轉換 DTO ↔ Entity 的工具

	@Autowired
	private UserService userService; // 取得目前登入使用者的資訊

	@PostMapping
	public ResponseEntity<String> addBloodSugar(@RequestBody BloodSugarRecordDTO dto) {
		System.out.println("📦 收到的 DTO：" + dto);
		System.out.println("➡️ recordDate 是否有值？" + dto.getRecordDate());

		User currentUser = userService.getCurrentLoginUser();
		dto.setAccountId(currentUser.getAccount().getId());
		bloodSugarService.save(dto);
		return ResponseEntity.ok("新增成功！");
	}

//	// 新增血糖紀錄
//	@PostMapping
//	public ResponseEntity<String> addBloodSugar(@RequestBody BloodSugarRecordDTO dto) {
//		// 取得目前登入者的帳號 ID，並設給 DTO，確認資料屬於誰
//		User currentUser = userService.getCurrentLoginUser();
//		dto.setAccountId(currentUser.getAccount().getId());
//
//		bloodSugarService.save(dto); // 傳 DTO，不是 Entity
//		return ResponseEntity.ok("新增成功！"); // 呼叫 Service 儲存資料，並回傳成功訊息（狀態碼 200 + 字串）
//	}

	// 查詢所有紀錄
	@GetMapping
	public ResponseEntity<List<BloodSugarRecordDTO>> findAll() {
		User currentUser = userService.getCurrentLoginUser(); // 根據登入者的帳號 ID 查詢資料，傳回 DTO 清單
		List<BloodSugarRecordDTO> dtoList = bloodSugarService.findByUserId(currentUser.getAccount().getId()); // 改用accountId查詢
		return ResponseEntity.ok(dtoList); // 回傳資料與 HTTP 200 成功狀態。
	}

	// 更新血糖紀錄
	@PutMapping("/{id}")
	public ResponseEntity<String> update(@PathVariable Integer id, @RequestBody BloodSugarRecordDTO dto) {
		// 先找出這筆原本的紀錄（用來做驗證或確認）
		BloodSugarRecordDTO originalDto = bloodSugarService.findById(id); // 回傳 DTO

		// 如果紀錄不存在，回傳 404（Not Found）
		if (originalDto == null) {
			return ResponseEntity.notFound().build();
		}

		System.out.println("🛠️ 更新請求的 ID：" + id);
		System.out.println("🛠️ 查詢到的原始 DTO：" + originalDto);
		dto.setAccountId(originalDto.getAccountId()); // 保留原本的帳號 ID 並設定正確的紀錄 ID
		dto.setRecordId(id);
		bloodSugarService.save(dto); // 用同一個 save 方法來儲存更新後的資料
		return ResponseEntity.ok("更新成功！");
	}

	// 刪除紀錄
	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		bloodSugarService.delete(id);
		return ResponseEntity.ok("刪除成功！"); // 呼叫 Service 刪除後，回傳成功訊息
	}
}
