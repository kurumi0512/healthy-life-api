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

import com.example.demo.model.dto.BloodPressureRecordDTO;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.BloodPressureRecordService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/rest/health/bp")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class BloodPressureRecordController {

	@Autowired
	private BloodPressureRecordService bpRecordService;

	@PostMapping
	public ApiResponse<?> saveRecord(@RequestBody BloodPressureRecordDTO dto, HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		dto.setAccountId(accountId);
		bpRecordService.saveRecord(dto);
		return ApiResponse.success("新增成功", null);
	}

	@GetMapping("/recent")
	public ApiResponse<List<BloodPressureRecordDTO>> getRecentRecords(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		List<BloodPressureRecordDTO> records = bpRecordService.getRecentRecords(accountId);
		return ApiResponse.success("查詢成功", records);
	}

	@PutMapping("/{recordId}")
	public ApiResponse<?> updateRecord(@PathVariable Integer recordId, @RequestBody BloodPressureRecordDTO dto,
			HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		dto.setAccountId(accountId);
		dto.setRecordId(recordId);
		bpRecordService.updateRecord(dto);
		return ApiResponse.success("更新成功", null);
	}

	@DeleteMapping("/{recordId}")
	public ApiResponse<?> deleteRecord(@PathVariable Integer recordId, HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		bpRecordService.deleteRecord(recordId, accountId);
		return ApiResponse.success("刪除成功", null);
	}

	@GetMapping
	public ApiResponse<List<BloodPressureRecordDTO>> getAllRecords(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		List<BloodPressureRecordDTO> records = bpRecordService.getAllRecords(accountId);
		return ApiResponse.success("查詢所有血壓紀錄成功", records);
	}

}
