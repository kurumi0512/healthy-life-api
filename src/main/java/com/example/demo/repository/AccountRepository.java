package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.Account;

//負責操作 Account 資料表的 Repository 介面
//繼承 JpaRepository，提供基本的 CRUD 操作
public interface AccountRepository extends JpaRepository<Account, Integer> {

	// 根據使用者名稱查詢帳號（回傳 Optional，可避免 null）
	Optional<Account> findByUsername(String username);

	// 根據 Email 查詢帳號（通常用於登入或驗證帳號）
	Optional<Account> findByEmail(String email);

	// 查詢所有符合 Email 的帳號（若 Email 不唯一才需要）
	List<Account> findAllByEmail(String email);
}
