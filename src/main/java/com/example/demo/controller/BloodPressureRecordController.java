package com.example.demo.controller;

import java.time.LocalDate;
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

import com.example.demo.model.dto.BloodPressureRecordDTO;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.BloodPressureRecordService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/rest/health/bp")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")

//新增、查詢、更新、刪除血壓紀錄,Session 驗證
public class BloodPressureRecordController {

	@Autowired
	private BloodPressureRecordService bpRecordService;

	// 新增血壓紀錄,null表示不回傳實際的資料
	@PostMapping
	public ApiResponse<?> saveRecord(@RequestBody BloodPressureRecordDTO dto, HttpSession session) {
		try {
			Integer accountId = (Integer) session.getAttribute("accountId");

			if (accountId == null) {
				return ApiResponse.error("未登入，請重新登入後再嘗試");
			}

			dto.setAccountId(accountId);

			// 若日期沒填，預設為今天
			if (dto.getRecordDate() == null) {
				dto.setRecordDate(LocalDate.now());
			}

			bpRecordService.saveRecord(dto);

			return ApiResponse.success("新增成功", null);
		} catch (Exception e) {
			e.printStackTrace(); // 顯示詳細錯誤（方便 Debug）
			return ApiResponse.error("新增失敗：" + e.getMessage());
		}
	}

	// 查詢所有血壓紀錄
	@GetMapping
	public ApiResponse<List<BloodPressureRecordDTO>> getAllRecords(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		List<BloodPressureRecordDTO> records = bpRecordService.getAllRecords(accountId);
		return ApiResponse.success("查詢所有血壓紀錄成功", records);
	}

	// 更新血壓紀錄
	// 前端送來的 JSON 資料會自動轉換成 DTO（含收縮壓、舒張壓、日期、備註等）
	// 從 Session 中取得目前登入者的 accountId
	@PutMapping("/{recordId}")
	public ApiResponse<?> updateRecord(@PathVariable Integer recordId, @RequestBody BloodPressureRecordDTO dto,
			HttpSession session) {

		Integer accountId = (Integer) session.getAttribute("accountId"); // 從 Session 取出當前使用者的帳號 ID。
		dto.setAccountId(accountId); // 將這個 ID 放進 DTO，確保只能編輯屬於自己的資料。
		dto.setRecordId(recordId); // 將 URL 中的 recordId 加入 DTO，指定這是要更新哪一筆紀錄。
		bpRecordService.updateRecord(dto);
		return ApiResponse.success("更新成功", null);
	}

	// 刪除血壓紀錄,呼叫 service 刪除該筆紀錄
	@DeleteMapping("/{recordId}")
	public ApiResponse<?> deleteRecord(@PathVariable Integer recordId, HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		bpRecordService.deleteRecord(recordId, accountId);
		return ApiResponse.success("刪除成功", null);
	}

	@GetMapping("/latest")
	public ResponseEntity<BloodPressureRecordDTO> getLatestRecord(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		BloodPressureRecordDTO latest = bpRecordService.findLatestByUserId(accountId);

		if (latest != null) {
			return ResponseEntity.ok(latest);
		} else {
			return ResponseEntity.noContent().build(); // 沒資料回傳 204 No Content
		}
	}

}
