package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
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
import com.example.demo.service.impl.BloodPressureRecordServiceImpl;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/rest/health/bp")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")

//新增、查詢、更新、刪除血壓紀錄,Session 驗證
public class BloodPressureRecordController {

	@Autowired
	private BloodPressureRecordServiceImpl bpRecordService;

	// 新增血壓紀錄,null表示不回傳實際的資料
	@PostMapping
	public ApiResponse<?> saveRecord(@Valid @RequestBody BloodPressureRecordDTO dto, BindingResult result,
			HttpSession session) {
		try {
			// 1️⃣ 表單驗證失敗就立即回傳錯誤訊息
			if (result.hasErrors()) {
				String message = result.getAllErrors().get(0).getDefaultMessage();
				return ApiResponse.error("輸入錯誤：" + message);
			}

			// 2️⃣ 檢查登入狀態
			Integer accountId = (Integer) session.getAttribute("accountId");
			if (accountId == null) {
				return ApiResponse.error("未登入，請重新登入後再嘗試");
			}

			dto.setAccountId(accountId);
			// 4️⃣ 執行新增邏輯
			bpRecordService.saveRecord(dto);

			return ApiResponse.success("新增成功", null);

		} catch (Exception e) {
			e.printStackTrace();
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
	public ApiResponse<BloodPressureRecordDTO> getLatestRecord(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		BloodPressureRecordDTO latest = bpRecordService.findLatestByUserId(accountId);

		if (latest != null) {
			return ApiResponse.success("查詢成功", latest); // ✅ 包進 data
		} else {
			return ApiResponse.error("尚無紀錄");
		}
	}

}
