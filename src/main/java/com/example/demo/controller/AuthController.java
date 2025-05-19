package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.LoginRequest;
import com.example.demo.model.dto.RegisterRequest;
import com.example.demo.service.AccountService;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/rest")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private AccountService accountService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
		String username = loginRequest.getUsername();
		String password = loginRequest.getPassword();

		boolean valid = authService.validate(username, password);
		if (valid) {
			session.setAttribute("user", username);
			return ResponseEntity.ok(Map.of("message", "登入成功", "user", username));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "帳號或密碼錯誤"));
		}
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
		if (accountService.isUsernameTaken(request.getUsername())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "帳號已存在"));
		}

		accountService.register(request.getUsername(), request.getPassword(), request.getEmail());

		return ResponseEntity.ok(Map.of("message", "註冊成功"));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpSession session) {
		session.invalidate();
		return ResponseEntity.ok(Map.of("message", "已登出"));
	}

	@GetMapping("/user")
	public ResponseEntity<?> getCurrentUser(HttpSession session) {
		String user = (String) session.getAttribute("user");
		if (user != null) {
			return ResponseEntity.ok(Map.of("user", user));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "尚未登入"));
		}
	}
}
