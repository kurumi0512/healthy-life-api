package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.WeightRecordDTO;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.impl.WeightRecordServiceImpl;

import jakarta.servlet.http.HttpSession;

@RestController // REST API 控制器，回傳 JSON 而非 HTML
@RequestMapping("/rest/health/weight") // API 路徑
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") // 允許跨來源請求

//負責體重紀錄管理的控制器
public class WeightRecordController {

	@Autowired // 依賴注入
	private WeightRecordServiceImpl weightRecordService;

	// [GET] 查詢目前使用者的所有體重紀錄
	@GetMapping
	public ApiResponse<List<WeightRecordDTO>> getAllRecords(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId"); // session 取出目前登入者的 accountId
		List<WeightRecordDTO> records = weightRecordService.getRecordsByAccountId(accountId); // 根據此帳號查詢所有體重紀錄
		return ApiResponse.success("查詢成功", records);
	}

	// [POST] 新增一筆體重紀錄（含體重、身高、記錄日期等）
	@PostMapping
	public ApiResponse<?> saveRecord(@RequestBody WeightRecordDTO dto, HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		dto.setAccountId(accountId); // 將使用者帳號 ID 設進 DTO
		weightRecordService.saveRecord(dto); // 儲存資料
		return ApiResponse.success("新增成功", null);
	}

	// [PUT] 更新指定紀錄 ID 的體重資料
	@PutMapping("/{recordId}")
	public ApiResponse<?> updateRecord(@PathVariable Integer recordId, @RequestBody WeightRecordDTO dto,
			HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId"); // 將這個 ID 及目前使用者的帳號 ID 塞入 DTO
		dto.setAccountId(accountId);
		dto.setRecordId(recordId);
		weightRecordService.updateRecord(dto); // 更新紀錄（由 service 驗證是否為自己的紀錄）
		return ApiResponse.success("修改成功", null);
	}

	// [DELETE] 刪除指定紀錄 ID 的體重資料
	@DeleteMapping("/{recordId}")
	public ApiResponse<?> deleteRecord(@PathVariable Integer recordId, HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId"); // 取得目前登入者的帳號 ID
		weightRecordService.deleteRecord(recordId, accountId);
		return ApiResponse.success("刪除成功", null);
	}

	// [GET] 查詢最近 5 筆體重紀錄（for 圖表、歷史區塊）
	@GetMapping("/recent")
	public ApiResponse<List<WeightRecordDTO>> getRecentRecords(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId"); // 取得目前登入者的帳號 ID
		List<WeightRecordDTO> records = weightRecordService.getRecent5RecordsByAccountId(accountId);
		return ApiResponse.success("查詢成功", records);
	}

	// [GET] 查詢最新一筆體重紀錄（for 複製上一次）
	@GetMapping("/latest")
	public ApiResponse<?> getLatestRecord(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		Optional<WeightRecordDTO> latestOpt = weightRecordService.getLatestRecordByAccountId(accountId);

		// 若有資料就回傳，否則傳回「尚無紀錄」
		if (latestOpt.isPresent()) {
			return ApiResponse.success("查詢成功", latestOpt.get());
		} else {
			return ApiResponse.success("尚無紀錄", null);
		}
	}
}