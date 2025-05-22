package com.example.demo.controller;

import java.net.URI;
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

import com.example.demo.exception.CertException;
import com.example.demo.model.dto.LoginRequest;
import com.example.demo.model.dto.RegisterRequest;
import com.example.demo.model.dto.UserCert;
import com.example.demo.service.AccountService;
import com.example.demo.service.AuthService;
import com.example.demo.service.EmailService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/rest/health")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private EmailService emailService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpSession session) {
		if (accountService.isUsernameTaken(request.getUsername())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "帳號已存在"));
		}

		// 儲存帳號資料
		accountService.register(request.getUsername(), request.getPassword(), request.getEmail());

		// ✅ 設定 session（模擬登入狀態）
		session.setAttribute("user", request.getUsername());

		// ✅ 建立驗證信的連結（可選）
		String confirmUrl = "http://localhost:8082/health/email/confirm?username=" + request.getUsername();
		emailService.sendEmail(request.getEmail(), "請點擊驗證連結：" + confirmUrl);

		// ✅ 回傳訊息（可以附上 session 狀態資料）
		return ResponseEntity.ok(Map.of("message", "註冊成功，已自動登入", "user", request.getUsername()));
	}

	// ✅ 修改為不依賴 Session 的 Email 驗證
	@GetMapping("/email/confirm")
	public ResponseEntity<?> confirmEmail(@RequestParam String username) {
		accountService.activateAccount(username);
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("http://localhost:5173/verify-success"))
				.build();
	}

	// ✅ 登入時驗證 session 中的驗證碼
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
		String sessionCode = (String) session.getAttribute("captcha"); // 後端驗證碼

		if (sessionCode == null || !sessionCode.equalsIgnoreCase(request.getCaptcha())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "驗證碼錯誤"));
		}

		try {
			UserCert cert = authService.validate(request.getUsername(), request.getPassword());

			session.setAttribute("cert", cert);
			session.setAttribute("user", cert.getUsername());
			session.setAttribute("accountId", cert.getAccountId()); // ✅ 這裡改了

			return ResponseEntity.ok(Map.of("message", "登入成功", "user",
					Map.of("id", cert.getAccountId(), "username", cert.getUsername(), "role", cert.getRole() // ✅ 加這一行
					)));
		} catch (CertException e) {
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpSession session) {
		session.invalidate();
		return ResponseEntity.ok(Map.of("message", "已登出"));
	}

	@GetMapping("/user")
	public ResponseEntity<?> currentUser(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		String username = (String) session.getAttribute("user");
		String role = null;
		UserCert cert = (UserCert) session.getAttribute("cert");
		if (cert != null) {
			role = cert.getRole();
		}

		if (accountId != null && username != null) {
			return ResponseEntity.ok(Map.of("user", Map.of("username", username, "id", accountId, "role", role)));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "尚未登入"));
		}
	}

	@GetMapping("/check")
	public Map<String, Object> checkLogin(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		String username = (String) session.getAttribute("user");

		if (accountId != null && username != null) {
			return Map.of("loggedIn", true, "user", Map.of("accountId", accountId, "username", username));
		} else {
			return Map.of("loggedIn", false);
		}
	}
}