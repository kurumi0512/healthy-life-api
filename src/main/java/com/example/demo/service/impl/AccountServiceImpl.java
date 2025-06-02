package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.Role;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
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
}