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

//帳號管理（註冊、啟用帳號）
//負責帳號的「建立、狀態控制」，例如註冊、新增使用者、驗證帳號、查詢帳號。
@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailServiceImpl emailService;

	// 新增帳號（含密碼加鹽 + 雜湊），建立對應的 User 物件
	// 由於密碼要額外加密、salt 要另外產生，不能直接用 Mapper 套過去，所以這裡手動處理是比較好的方式。
	@Override
	public void register(String username, String password, String email) {
		String salt = HashUtil.generateSalt(); // 先產生 salt，再加密 hashPassword
		String hashPassword = HashUtil.hashPassword(password, salt);

		Account account = new Account();
		account.setUsername(username);
		account.setEmail(email);
		account.setHashSalt(salt);
		account.setHashPassword(hashPassword);
		account.setCompleted(false); // 尚未驗證
		account.setStatus("UNVERIFIED"); // 初始狀態
		account.setRole(Role.USER); // 預設使用者角色
		account.setCreateTime(LocalDateTime.now());

		accountRepository.save(account); // 儲存至資料庫

		// ✅ 建立對應的 User，name 預設為 username
		User user = new User();
		user.setAccount(account); // ✅ 關聯到剛剛儲存的帳號
		user.setCreateTime(LocalDateTime.now());
		user.setName(username); // ✅ 將帳號當作預設姓名
		userRepository.save(user); // ✅ 儲存 user
	}

	@Override
	public boolean isUsernameTaken(String username) {
		return accountRepository.findByUsername(username).isPresent();
	}

	@Override
	public void activateAccount(String username) {
		Optional<Account> optional = accountRepository.findByUsername(username);
		if (optional.isPresent()) {
			Account account = optional.get();
			account.setCompleted(true); // ✅ 標記為已完成驗證
			account.setStatus("active"); // ✅ 狀態改為有效
			accountRepository.save(account);
		} else {
			throw new RuntimeException("查無帳號：" + username);
		}
	}

	@Override
	public Account findById(Integer id) {
		return accountRepository.findById(id).orElseThrow(() -> new RuntimeException("找不到帳號 ID：" + id));
	}

	@Override
	public void sendResetCode(String email) {
		List<Account> accounts = accountRepository.findAllByEmail(email);
		if (accounts.isEmpty()) {
			throw new RuntimeException("查無此 Email");
		}

		Account account = accounts.stream().filter(a -> a.getResetSecret() == null).findFirst().orElse(accounts.get(0)); // 若都已設過就拿第一筆

		// 若尚未有 resetSecret 就產生一組
		if (account.getResetSecret() == null) {
			String secret = Base64.getEncoder()
					.encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
			account.setResetSecret(secret);
			accountRepository.save(account);
		}

		try {
			long interval = System.currentTimeMillis() / 1000L / 30L;
			String code = KeyUtil.generateTOTP(account.getResetSecret(), interval, "HmacSHA256");

			emailService.sendResetCode(email, code);
		} catch (Exception e) {
			e.printStackTrace(); // 方便 debug 看真正錯誤
			throw new RuntimeException("TOTP 產生失敗", e);
		}
	}

	@Override
	public boolean verifyResetCode(String email, String code) {
		List<Account> accounts = accountRepository.findAllByEmail(email);
		if (accounts.isEmpty())
			return false;

		Account account = accounts.get(0); // ✅ 取第一筆資料

		String secret = account.getResetSecret();
		if (secret == null)
			return false;

		long interval = System.currentTimeMillis() / 1000L / 30L;

		try {
			for (long i = -1; i <= 1; i++) {
				String expected = KeyUtil.generateTOTP(secret, interval + i, "HMACSHA256");
				if (expected.equals(code))
					return true;
			}
		} catch (Exception e) {
			return false;
		}

		return false;
	}

	@Override
	public void resetPassword(String email, String newPassword) {
		List<Account> accounts = accountRepository.findAllByEmail(email);
		if (accounts.isEmpty()) {
			throw new RuntimeException("找不到帳號");
		}
		Account account = accounts.get(0); // ✅ 取第一筆帳號

		String salt = HashUtil.generateSalt();
		String hashed = HashUtil.hashPassword(newPassword, salt);

		account.setHashSalt(salt);
		account.setHashPassword(hashed);
		account.setResetSecret(null); // 清除 resetSecret，避免重複使用
		accountRepository.save(account);
	}

}