package com.example.demo.service;

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
	 * @return UserCert 登入後的憑證（userId, username, role）
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

		// 🔧 改：允許 user 為 null，登入成功但提醒前端補資料
		User user = userRepository.findByAccountId(account.getId()).orElse(null);

		String name = user != null ? user.getName() : null;
		String email = account.getEmail(); // Account 不會為 null

		// ✅ 照樣登入，回傳憑證
		return new UserCert(account.getId(), account.getUsername(), account.getRole().name(), name, email);
	}
}