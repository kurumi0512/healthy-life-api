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

//血壓紀錄控制器：處理新增、查詢、更新、刪除紀錄
//每筆操作都需通過 Session 驗證，確認是登入者本人
public class BloodPressureRecordController {

	@Autowired
	private BloodPressureRecordServiceImpl bpRecordService;

	// [POST] 新增血壓紀錄
	// 限登入使用者操作，並驗證欄位是否合法（收縮壓、舒張壓、日期等）
	@PostMapping
	public ApiResponse<?> saveRecord(@Valid @RequestBody BloodPressureRecordDTO dto, BindingResult result,
			HttpSession session) {
		try {
			// 1️.表單驗證（欄位不合法立即回應）
			if (result.hasErrors()) {
				String message = result.getAllErrors().get(0).getDefaultMessage();
				return ApiResponse.error("輸入錯誤：" + message);
			}

			// 2️.驗證使用者是否登入
			Integer accountId = (Integer) session.getAttribute("accountId");
			if (accountId == null) {
				return ApiResponse.error("未登入，請重新登入後再嘗試");
			}

			// 3️.將登入帳號資訊綁定到紀錄上（避免操作其他人資料）
			dto.setAccountId(accountId);

			// 4.呼叫 Service 儲存紀錄
			bpRecordService.saveRecord(dto);

			return ApiResponse.success("新增成功", null);

		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponse.error("新增失敗：" + e.getMessage());
		}
	}

	// [GET] 查詢目前使用者的所有血壓紀錄
	// 資料來源為該帳號所建立的紀錄
	@GetMapping
	public ApiResponse<List<BloodPressureRecordDTO>> getAllRecords(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		List<BloodPressureRecordDTO> records = bpRecordService.getAllRecords(accountId);
		return ApiResponse.success("查詢所有血壓紀錄成功", records);
	}

	// [PUT] 修改指定的血壓紀錄
	// 僅允許修改自己帳號所屬的紀錄（透過 session 綁定）
	@PutMapping("/{recordId}")
	public ApiResponse<?> updateRecord(@PathVariable Integer recordId, @RequestBody BloodPressureRecordDTO dto,
			HttpSession session) {

		Integer accountId = (Integer) session.getAttribute("accountId"); // 從 Session 取得目前帳號
		dto.setAccountId(accountId); // 綁定帳號到 DTO，避免修改非本人資料
		dto.setRecordId(recordId); // 指定要更新哪一筆紀錄
		bpRecordService.updateRecord(dto); // 執行更新
		return ApiResponse.success("更新成功", null);
	}

	// [DELETE] 刪除指定紀錄
	// 只能刪除自己帳號的紀錄
	@DeleteMapping("/{recordId}")
	public ApiResponse<?> deleteRecord(@PathVariable Integer recordId, HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		bpRecordService.deleteRecord(recordId, accountId); // 帳號 ID 傳入確認歸屬權
		return ApiResponse.success("刪除成功", null);
	}

	// [GET] 查詢最新一筆血壓紀錄
	// 用於前端快速填入上一筆資料的輔助功能
	@GetMapping("/latest")
	public ApiResponse<BloodPressureRecordDTO> getLatestRecord(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		BloodPressureRecordDTO latest = bpRecordService.findLatestByUserId(accountId);

		if (latest != null) {
			return ApiResponse.success("查詢成功", latest);
		} else {
			return ApiResponse.error("尚無紀錄");
		}
	}

}
