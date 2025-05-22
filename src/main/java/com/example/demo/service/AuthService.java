package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.CertException;
import com.example.demo.model.dto.UserCert;
import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.HashUtil;

@Service
public class AuthService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UserRepository userRepository;

	/**
	 * 驗證帳號密碼，成功後回傳使用者憑證
	 * 
	 * @param username    使用者名稱
	 * @param rawPassword 明文密碼
	 * @return UserCert 登入後的憑證
	 * @throws CertException 帳號或密碼錯誤
	 */
	public UserCert validate(String username, String rawPassword) throws CertException {
		Optional<Account> optAccount = accountRepository.findByUsername(username);
		if (optAccount.isEmpty()) {
			throw new CertException("帳號不存在");
		}

		Account account = optAccount.get();
		String inputHash = HashUtil.hashPassword(rawPassword, account.getHashSalt());

		if (!inputHash.equals(account.getHashPassword())) {
			throw new CertException("密碼錯誤");
		}

		// ✅ 更新最後登入時間
		account.setLastLogin(LocalDateTime.now());
		accountRepository.save(account);

		// ✅ 抓取對應 User 資料
		User user = userRepository.findByAccountId(account.getId()).orElse(null);

		// ✅ 封裝成 UserCert DTO 回傳
		return toUserCert(account, user);
	}

	/**
	 * 將 Account 與 User 資料轉換為 UserCert 憑證 DTO
	 */
	private UserCert toUserCert(Account account, User user) {
		boolean userCompleted = user != null && user.getAge() != null && user.getGender() != null;

		return new UserCert(account.getId(), account.getUsername(), account.getRole().name(),
				user != null ? user.getName() : null, account.getEmail(), account.getCompleted(),
				"ADMIN".equals(account.getRole().name()), userCompleted // ✅ 加入 userCompleted 欄位
		);
	}
}
