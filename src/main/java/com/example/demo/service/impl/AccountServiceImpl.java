package com.example.demo.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.Role;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.KeyUtil;
import com.example.demo.service.AccountService;
import com.example.demo.util.HashUtil;

@Service // 標記為 Spring 管理的 Service 類別
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailServiceImpl emailService;

	// 註冊帳號：建立 Account 和對應的 User
	@Override
	public void register(String username, String password, String email) {
		// 密碼加鹽＋雜湊處理
		String salt = HashUtil.generateSalt();
		String hashPassword = HashUtil.hashPassword(password, salt);

		// 建立帳號
		Account account = new Account();
		account.setUsername(username);
		account.setEmail(email);
		account.setHashSalt(salt);
		account.setHashPassword(hashPassword);
		account.setCompleted(false); // 尚未驗證
		account.setStatus("UNVERIFIED"); // 狀態初始為未啟用
		account.setRole(Role.USER); // 預設角色
		account.setCreateTime(LocalDateTime.now());

		accountRepository.save(account); // 儲存帳號

		// 建立對應使用者資料（User）
		User user = new User();
		user.setAccount(account); // 關聯帳號
		user.setName(username); // 預設姓名為帳號
		user.setCreateTime(LocalDateTime.now());

		userRepository.save(user); // 儲存使用者資料
	}

	// 檢查帳號是否已被註冊
	@Override
	public boolean isUsernameTaken(String username) {
		return accountRepository.findByUsername(username).isPresent();
	}

	// 啟用帳號（驗證完成後呼叫）
	@Override
	public void activateAccount(String username) {
		Optional<Account> optional = accountRepository.findByUsername(username);
		if (optional.isPresent()) {
			Account account = optional.get();
			account.setCompleted(true); // 標記為已完成驗證
			account.setStatus("active"); // 帳號狀態轉為 active
			accountRepository.save(account);
		} else {
			throw new RuntimeException("查無帳號：" + username);
		}
	}

	// 根據帳號 ID 查詢帳號
	@Override
	public Account findById(Integer id) {
		return accountRepository.findById(id).orElseThrow(() -> new RuntimeException("找不到帳號 ID：" + id));
	}

	// 寄送忘記密碼的驗證碼（TOTP）
	@Override
	public void sendResetCode(String email) {
		List<Account> accounts = accountRepository.findAllByEmail(email);
		if (accounts.isEmpty()) {
			throw new RuntimeException("查無此 Email");
		}

		// 取第一個尚未設 resetSecret 的帳號，否則就拿第一筆
		Account account = accounts.stream().filter(a -> a.getResetSecret() == null).findFirst().orElse(accounts.get(0));

		// 若沒有 secret 就產生一組 UUID（Base64 編碼）
		if (account.getResetSecret() == null) {
			String secret = Base64.getEncoder()
					.encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
			account.setResetSecret(secret);
			accountRepository.save(account);
		}

		// 根據時間產生 6 碼驗證碼（TOTP）
		try {
			long interval = System.currentTimeMillis() / 1000L / 300L; // 每 300 秒一組
			String code = KeyUtil.generateTOTP(account.getResetSecret(), interval, "HmacSHA256");

			emailService.sendResetCode(email, code); // 寄送驗證碼
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("TOTP 產生失敗", e);
		}
	}

	// 驗證使用者輸入的 TOTP 是否正確（允許前後 30 秒誤差）
	@Override
	public boolean verifyResetCode(String email, String code) {
		List<Account> accounts = accountRepository.findAllByEmail(email);
		if (accounts.isEmpty())
			return false;

		Account account = accounts.get(0); // 拿第一筆帳號
		String secret = account.getResetSecret();
		if (secret == null)
			return false;

		long interval = System.currentTimeMillis() / 1000L / 300L;

		try {
			for (long i = -1; i <= 1; i++) { // 可容許時間誤差 ±30 秒
				String expected = KeyUtil.generateTOTP(secret, interval + i, "HMACSHA256");
				if (expected.equals(code))
					return true;
			}
		} catch (Exception e) {
			return false;
		}

		return false;
	}

	// 重設密碼（驗證碼正確後呼叫）
	@Override
	public void resetPassword(String email, String newPassword) {
		List<Account> accounts = accountRepository.findAllByEmail(email);

		if (accounts.isEmpty()) {
			System.out.println("[重設密碼] 找不到帳號：" + email);
			throw new RuntimeException("找不到帳號");
		}

		Account account = accounts.get(0);

		// 印出更新前的資訊
		System.out.println("[重設密碼] 原密碼: " + account.getHashPassword());
		System.out.println("[重設密碼] 原 salt: " + account.getHashSalt());
		System.out.println("[重設密碼] 原 resetSecret: " + account.getResetSecret());

		// 雜湊新密碼
		String salt = HashUtil.generateSalt();
		String hashed = HashUtil.hashPassword(newPassword, salt);

		// 檢查加密是否成功
		if (hashed == null || hashed.isEmpty()) {
			System.out.println("[重設密碼] 錯誤：hashPassword 為空！");
			throw new RuntimeException("密碼加密失敗");
		}

		// 更新帳號欄位
		account.setHashSalt(salt);
		account.setHashPassword(hashed);
		account.setResetSecret(null);

		// 強制更新資料庫
		accountRepository.saveAndFlush(account);

		// 印出更新後的資訊
		System.out.println("[重設密碼] 新密碼: " + account.getHashPassword());
		System.out.println("[重設密碼] 新 salt: " + account.getHashSalt());
		System.out.println("[重設密碼] 新 resetSecret: " + account.getResetSecret());

		System.out.println("[重設密碼] 密碼已成功更新");
	}
}