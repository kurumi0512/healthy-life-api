package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.UserDto;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/rest")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {

	@Autowired
	private UserService userService;

	// [GET] 查詢所有使用者（後台清單）
	// 用於後台會員管理頁，顯示所有使用者資訊
	@GetMapping("/users")
	public ResponseEntity<List<UserDto>> findAllUsers() {
		return ResponseEntity.ok(userService.findAllUsers());
	}

	// [GET] 查詢單一使用者資料（for 編輯頁）
	// 用於編輯會員時預填表單資料
	@GetMapping("/user/{id}")
	public ResponseEntity<UserDto> findUserById(@PathVariable Integer id) {
		return ResponseEntity.ok(userService.findUserById(id));
	}

	// [PUT] 修改使用者資料（for 管理員後台）
	// 依使用者 ID 修改基本資料，例如姓名、信箱、角色
	@PutMapping("/user/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UserDto userDto) {
		userService.updateUser(id, userDto);
		return ResponseEntity.ok(Map.of("message", "使用者資料更新成功"));
	}

	// [DELETE] 軟刪除使用者（將狀態改為 INACTIVE）
	// 不是真正刪除資料，而是標記為停用，避免影響關聯資料
	@DeleteMapping("/user/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
		userService.deleteUser(id);
		return ResponseEntity.ok(Map.of("message", "使用者已刪除"));
	}
}