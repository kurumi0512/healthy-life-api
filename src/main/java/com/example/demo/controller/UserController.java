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

	// ✅ 查詢所有使用者（後台）
	@GetMapping("/users")
	public ResponseEntity<List<UserDto>> findAllUsers() {
		return ResponseEntity.ok(userService.findAllUsers());
	}

	// ✅ 查詢單一使用者資料（for 編輯）
	@GetMapping("/user/{id}")
	public ResponseEntity<UserDto> findUserById(@PathVariable Integer id) {
		return ResponseEntity.ok(userService.findUserById(id));
	}

	// ✅ 修改使用者資料
	@PutMapping("/user/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UserDto userDto) {
		userService.updateUser(id, userDto);
		return ResponseEntity.ok(Map.of("message", "使用者資料更新成功"));
	}

	// ✅ 軟刪除（將 status 改為 INACTIVE）
	@DeleteMapping("/user/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
		userService.deleteUser(id);
		return ResponseEntity.ok(Map.of("message", "使用者已刪除"));
	}
}