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
import com.example.demo.model.dto.LoginCheckResponse;
import com.example.demo.model.dto.LoginRequest;
import com.example.demo.model.dto.RegisterRequest;
import com.example.demo.model.dto.UserCert;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.dto.UserSimpleDto;
import com.example.demo.model.entity.Account;
import com.example.demo.service.AccountService;
import com.example.demo.service.UserService;
import com.example.demo.service.impl.AuthServiceImpl;
import com.example.demo.service.impl.EmailServiceImpl;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/rest/health")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
//負責帳號註冊、登入、登出、驗證與查詢登入狀態的控制器（Controller）
public class AuthController {

	@Autowired // 登入驗證（Login）
	private AuthServiceImpl authService;

	@Autowired
	private AccountService accountService; // 帳號管理（註冊、啟用帳號）

	@Autowired
	private EmailServiceImpl emailService; // email認證

	@Autowired
	private UserService userService; // 查詢使用者

	// 登入
	@PostMapping("/register") // @Valid欄位驗證,BindingResult會接收這些驗證檢查的結果 //HttpSession 專門幫某一位使用者存資料
	// ResponseEntity<?>表示 HTTP 回應的泛型
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult,
			HttpSession session) {

		// 驗證格式錯誤
		if (bindingResult.hasErrors()) {
			String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
			return ResponseEntity.badRequest().body(Map.of("message", errorMessage));
		}

		// 檢查帳號是否已存在
		// 409 Conflict：伺服器無法完成請求，因為請求內容與目前的資源狀態衝突
		if (accountService.isUsernameTaken(request.getUsername())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "帳號已存在"));
		}

		// 儲存帳號資料
		accountService.register(request.getUsername(), request.getPassword(), request.getEmail());

		// 設定 session（模擬登入）
		session.setAttribute("user", request.getUsername());

		// 發送驗證信
		String confirmUrl = "http://localhost:8082/rest/health/email/confirm?username=" + request.getUsername();
		emailService.sendEmail(request.getEmail(), request.getUsername()); // ✅ 正確：傳入純 username

		// 回傳訊息與登入資料
		return ResponseEntity.ok(Map.of("message", "註冊成功，請至信箱驗證帳號", "user", request.getUsername()));
	}

	// 修改為不依賴 Session 的 Email 驗證
	@GetMapping("/email/confirm")
	public RedirectView confirmEmail(@RequestParam String username) {
		try {
			accountService.activateAccount(username); // 嘗試啟用帳號,呼叫 Service 層處理
			return new RedirectView("http://localhost:5173/verify-success"); // 成功 → 導向成功頁
		} catch (Exception e) {
			return new RedirectView("http://localhost:5173/verify-fail"); // 失敗 → 導向失敗頁
		}
	}

	// 接收 JSON 格式的登入資料
	// 登入時驗證 session 中的驗證碼
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, BindingResult result,
			HttpSession session) {

		// 驗證格式錯誤
		if (result.hasErrors()) {
			String message = result.getAllErrors().get(0).getDefaultMessage();
			return ResponseEntity.badRequest().body(Map.of("message", message));
		}

		String sessionCode = (String) session.getAttribute("captcha");
		// 比較使用者輸入的驗證碼是否和 session 中的相同（不區分大小寫）
		// 錯誤就回傳 HTTP 401 未授權
		if (sessionCode == null || !sessionCode.equalsIgnoreCase(request.getCaptcha())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "驗證碼錯誤"));
		}
		// authService.validate(...)：調用驗證服務，確認帳號密碼正確。
		try {
			UserCert cert = authService.validate(request.getUsername(), request.getPassword());
			session.setAttribute("cert", cert);
			session.setAttribute("user", cert.getUsername());
			session.setAttribute("accountId", cert.getAccountId());
			return ResponseEntity.ok(Map.of("message", "登入成功", "user", cert));
		} catch (CertException e) {
			// 如果成功，就將重要資料（cert物件、帳號名稱、accountId）放入 session 中。
			return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpSession session) {
		session.invalidate(); // 清空 session,清空登入狀態
		return ResponseEntity.ok(Map.of("message", "已登出"));
	}

	@GetMapping("/user")
	// 從 session 中取出認證物件 UserCert
	public ResponseEntity<?> currentUser(HttpSession session) {
		UserCert cert = (UserCert) session.getAttribute("cert");

		// session有資料,就回傳帳號資料
		if (cert != null) {
			return ResponseEntity.ok(Map.of("user",
					Map.of("username", cert.getUsername(), "id", cert.getAccountId(), "role", cert.getRole())));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "尚未登入")); // 未登入回傳401未登入錯誤
		}
	}

	// 用 accountId 和 username 檢查是否還在登入狀態
	@GetMapping("/check")
	public ResponseEntity<LoginCheckResponse> checkLogin(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		String username = (String) session.getAttribute("user");

		if (accountId != null && username != null) {
			UserSimpleDto user = new UserSimpleDto(accountId, username);
			LoginCheckResponse response = new LoginCheckResponse(true, user);
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.ok(new LoginCheckResponse(false, null));
		}
	}

	// 取出「使用者個人資料（UserDto）,後臺管理帳號使用
	@GetMapping("/user/profile")
	public ResponseEntity<UserDto> getCurrentUserProfile(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		if (accountId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);// 沒登入表示未授權的請求
		}

		UserDto dto = userService.findByAccountId(accountId);
		return ResponseEntity.ok(dto);
	}

	// 取出帳號資料（AccountResponseDTO）,前台個人頁面
	@GetMapping("/account/profile")
	public ResponseEntity<AccountResponseDTO> getAccountProfile(HttpSession session) {
		Integer accountId = (Integer) session.getAttribute("accountId");
		if (accountId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}

		// 不直接回傳實體 Account，而是轉成精簡、對前端友善的 DTO，避免暴露敏感欄位（例如密碼、鹽值）
		Account account = accountService.findById(accountId);
		AccountResponseDTO dto = AccountMapper.toResponseDTO(account);
		return ResponseEntity.ok(dto);
	}

	@PostMapping("/forgot-password/send")
	public ResponseEntity<?> sendResetCode(@RequestBody Map<String, String> body) {
		String email = body.get("email");
		try {
			accountService.sendResetCode(email); // 呼叫剛剛建立的 Service 方法
			return ResponseEntity.ok(Map.of("message", "驗證碼已寄出，請至信箱查收"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "寄送失敗：" + e.getMessage()));
		}
	}

	@PostMapping("/forgot-password/verify")
	public ResponseEntity<?> verifyResetCode(@RequestBody Map<String, String> body) {
		String email = body.get("email");
		String code = body.get("code");

		boolean valid = accountService.verifyResetCode(email, code);
		if (valid) {
			return ResponseEntity.ok(Map.of("message", "驗證成功，請輸入新密碼"));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "驗證碼錯誤或已過期"));
		}
	}

	@PostMapping("/forgot-password/reset")
	public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
		String email = body.get("email");
		String newPassword = body.get("newPassword");

		try {
			accountService.resetPassword(email, newPassword);
			return ResponseEntity.ok(Map.of("message", "密碼已成功重設"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "密碼重設失敗：" + e.getMessage()));
		}
	}

}