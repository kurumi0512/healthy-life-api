package com.example.demo.controller;

import java.util.List;

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
import com.example.demo.service.WeightRecordService;

import jakarta.servlet.http.HttpSession;

@RestController // 表示這是一個 REST API 控制器，方法回傳的會是 JSON（非網頁畫面）
@RequestMapping("/rest/health/weight") // API 路徑
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") // 允許跨來源請求

//負責體重紀錄管理的控制器
public class WeightRecordController {

	@Autowired // 依賴注入
	private WeightRecordService weightRecordService;

	// 查詢所有體重記錄
	@GetMapping
	public ApiResponse<List<WeightRecordDTO>> getAllRecords(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId"); // session 取出目前登入者的 accountId
		List<WeightRecordDTO> records = weightRecordService.getRecordsByAccountId(accountId); // 根據此帳號查詢所有體重紀錄
		return ApiResponse.success("查詢成功", records); // 回傳包裝過的API
	}

	// 新增體重記錄
	// requestBody是 {"id":123, "name":"Judy"...}
	@PostMapping
	public ApiResponse<?> saveRecord(@RequestBody WeightRecordDTO dto, HttpSession session) {
		// 前端傳來的 WeightRecordDTO（體重、新增日期、身高等資料）會自動映射進 dto
		Integer accountId = (Integer) session.getAttribute("accountId");
		dto.setAccountId(accountId); // 將 accountId 帶進 DTO
		weightRecordService.saveRecord(dto);
		return ApiResponse.success("新增成功", null);
	}

	// 更新特定體重紀錄
	@PutMapping("/{recordId}")
	public ApiResponse<?> updateRecord(@PathVariable Integer recordId, @RequestBody WeightRecordDTO dto,
			HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId"); // 將這個 ID 及目前使用者的帳號 ID 塞入 DTO
		dto.setAccountId(accountId);
		dto.setRecordId(recordId);
		weightRecordService.updateRecord(dto); // Service 更新該筆資料（也可以驗證是不是使用者自己的紀錄）
		return ApiResponse.success("修改成功", null);
	}

	// 刪除特定體重紀錄
	@DeleteMapping("/{recordId}")
	public ApiResponse<?> deleteRecord(@PathVariable Integer recordId, HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId"); // 取得目前登入者的帳號 ID
		weightRecordService.deleteRecord(recordId, accountId);
		return ApiResponse.success("刪除成功", null);
	}

	// 查詢最近 5 筆紀錄
	@GetMapping("/recent")
	public ApiResponse<List<WeightRecordDTO>> getRecentRecords(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId"); // 取得目前登入者的帳號 ID
		List<WeightRecordDTO> records = weightRecordService.getRecent5RecordsByAccountId(accountId);
		return ApiResponse.success("查詢成功", records);
	}

	// 查詢最新一筆紀錄
	@GetMapping("/latest")
	public ApiResponse<WeightRecordDTO> getLatestRecord(HttpSession session) { // 定義一個方法 getLatestRecord
		// 回傳型別是 ApiResponse<WeightRecordDTO>，也就是「一個包裝好的回應物件」，裡面放著
		// WeightRecordDTO（前端要的資料格式）
		Integer accountId = (Integer) session.getAttribute("accountId"); // 取得目前登入者的帳號 ID
		WeightRecordDTO latest = weightRecordService.getLatestRecordByAccountId(accountId); // 根據帳號查詢最新一筆體重資料（已轉為 DTO）
		return ApiResponse.success("查詢成功", latest); // 封裝成統一回應格式（訊息＋資料）
	}
}
