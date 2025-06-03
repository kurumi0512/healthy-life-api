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

@RestController
@RequestMapping("/rest/health/weight")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class WeightRecordController {

	@Autowired
	private WeightRecordService weightRecordService;

	@GetMapping
	public ApiResponse<List<WeightRecordDTO>> getAllRecords(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		List<WeightRecordDTO> records = weightRecordService.getRecordsByAccountId(accountId);
		return ApiResponse.success("查詢成功", records);
	}

	@PostMapping
	public ApiResponse<?> saveRecord(@RequestBody WeightRecordDTO dto, HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		dto.setAccountId(accountId); // 將 accountId 帶進 DTO
		weightRecordService.saveRecord(dto);
		return ApiResponse.success("新增成功", null);
	}

	@PutMapping("/{recordId}")
	public ApiResponse<?> updateRecord(@PathVariable Integer recordId, @RequestBody WeightRecordDTO dto,
			HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		dto.setAccountId(accountId);
		dto.setRecordId(recordId);
		weightRecordService.updateRecord(dto);
		return ApiResponse.success("修改成功", null);
	}

	@DeleteMapping("/{recordId}")
	public ApiResponse<?> deleteRecord(@PathVariable Integer recordId, HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		weightRecordService.deleteRecord(recordId, accountId);
		return ApiResponse.success("刪除成功", null);
	}

	@GetMapping("/recent")
	public ApiResponse<List<WeightRecordDTO>> getRecentRecords(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		List<WeightRecordDTO> records = weightRecordService.getRecent5RecordsByAccountId(accountId);
		return ApiResponse.success("查詢成功", records);
	}

	@GetMapping("/latest")
	public ApiResponse<WeightRecordDTO> getLatestRecord(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		WeightRecordDTO latest = weightRecordService.getLatestRecordByAccountId(accountId);
		return ApiResponse.success("查詢成功", latest);
	}
}
