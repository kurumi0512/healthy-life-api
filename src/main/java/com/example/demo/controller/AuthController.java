package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.example.demo.exception.CertException;
import com.example.demo.mapper.AccountMapper;
import com.example.demo.model.dto.AccountResponseDTO;
import com.example.demo.model.dto.LoginRequest;
import com.example.demo.model.dto.RegisterRequest;
import com.example.demo.model.dto.UserCert;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.Account;
import com.example.demo.service.AccountService;
import com.example.demo.service.AuthService;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/rest/health")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
//負責帳號註冊、登入、登出、驗證與查詢登入狀態的控制器（Controller）
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserService userService;

	// 登入
	@PostMapping("/register") // @Valid欄位驗證,BindingResult會接收這些驗證檢查的結果 //HttpSession 專門幫某一位使用者存資料
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult,
			HttpSession session) {

		// ✅ 驗證格式錯誤
		if (bindingResult.hasErrors()) {
			String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
			return ResponseEntity.badRequest().body(Map.of("message", errorMessage));
		}

		// ✅ 檢查帳號是否已存在
		// 409 Conflict：伺服器無法完成請求，因為請求內容與目前的資源狀態衝突
		if (accountService.isUsernameTaken(request.getUsername())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "帳號已存在"));
		}

		// ✅ 儲存帳號資料
		accountService.register(request.getUsername(), request.getPassword(), request.getEmail());

		// ✅ 設定 session（模擬登入）
		session.setAttribute("user", request.getUsername());

		// ✅ 發送驗證信
		String confirmUrl = "http://localhost:8082/rest/health/email/confirm?username=" + request.getUsername();
		emailService.sendEmail(request.getEmail(), "請點擊驗證連結：" + confirmUrl);

		// ✅ 回傳訊息與登入資料
		return ResponseEntity.ok(Map.of("message", "註冊成功，請至信箱驗證帳號", "user", request.getUsername()));
	}

	// ✅ 修改為不依賴 Session 的 Email 驗證
	@GetMapping("/email/confirm")
	public RedirectView confirmEmail(@RequestParam String username) {
		try {
			accountService.activateAccount(username); // ✅ 呼叫 Service 層處理
			return new RedirectView("http://localhost:5173/verify-success");
		} catch (Exception e) {
			return new RedirectView("http://localhost:5173/verify-fail");
		}
	}

	// ✅ 登入時驗證 session 中的驗證碼
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
		String sessionCode = (String) session.getAttribute("captcha");

		if (sessionCode == null || !sessionCode.equalsIgnoreCase(request.getCaptcha())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "驗證碼錯誤"));
		}

		try {
			UserCert cert = authService.validate(request.getUsername(), request.getPassword());

			session.setAttribute("cert", cert);
			session.setAttribute("user", cert.getUsername());
			session.setAttribute("accountId", cert.getAccountId());

			// ✅ 直接把完整 cert 當作 user 傳給前端
			return ResponseEntity.ok(Map.of("message", "登入成功", "user", cert));
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
		UserCert cert = (UserCert) session.getAttribute("cert");

		if (cert != null) {
			return ResponseEntity.ok(Map.of("user",
					Map.of("username", cert.getUsername(), "id", cert.getAccountId(), "role", cert.getRole())));
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

	@GetMapping("/user/profile")
	public ResponseEntity<UserDto> getCurrentUserProfile(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		if (accountId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		UserDto dto = userService.findByAccountId(accountId);
		return ResponseEntity.ok(dto);
	}

	@GetMapping("/account/profile")
	public ResponseEntity<AccountResponseDTO> getAccountProfile(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		if (accountId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		Account account = accountService.findById(accountId);
		AccountResponseDTO dto = AccountMapper.toResponseDTO(account);
		return ResponseEntity.ok(dto);
	}
}