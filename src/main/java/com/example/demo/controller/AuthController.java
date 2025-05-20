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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.LoginRequest;
import com.example.demo.model.dto.LoginResult;
import com.example.demo.model.dto.RegisterRequest;
import com.example.demo.service.AccountService;
import com.example.demo.service.AuthService;
import com.example.demo.service.EmailService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/health")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private EmailService emailService;

	// ✅ 註冊 + 發送驗證信
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
		if (accountService.isUsernameTaken(request.getUsername())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "帳號已存在"));
		}

		accountService.register(request.getUsername(), request.getPassword(), request.getEmail());

		String confirmUrl = "http://localhost:5173/health/email/confirm?username=" + request.getUsername();
		emailService.sendEmail(request.getEmail(), "請點擊驗證連結：" + confirmUrl);

		return ResponseEntity.ok(Map.of("message", "註冊成功，請至信箱完成驗證"));
	}

	// ✅ 修改為不依賴 Session 的 Email 驗證
	@GetMapping("/email/confirm")
	public ResponseEntity<?> confirmEmail(@RequestParam String username) {
		accountService.activateAccount(username);
		return ResponseEntity.ok(Map.of("message", "驗證成功，帳號已啟用"));
	}

	// ✅ 登入時驗證 session 中的驗證碼
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
		String sessionCode = (String) session.getAttribute("captcha"); // 後端驗證碼

		if (sessionCode == null || !sessionCode.equalsIgnoreCase(request.getCaptcha())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "驗證碼錯誤"));
		}

		LoginResult result = authService.validate(request.getUsername(), request.getPassword());

		if (!result.isSuccess()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", result.getMessage()));
		}

		session.setAttribute("user", result.getUsername());
		return ResponseEntity.ok(Map.of("message", result.getMessage(), "user", result.getUsername(), "id",
				result.getId(), "email", result.getEmail()));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpSession session) {
		session.invalidate();
		return ResponseEntity.ok(Map.of("message", "已登出"));
	}

	@GetMapping("/user")
	public ResponseEntity<?> currentUser(HttpSession session) {
		String user = (String) session.getAttribute("user");
		if (user != null) {
			return ResponseEntity.ok(Map.of("user", user));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "尚未登入"));
		}
	}
}