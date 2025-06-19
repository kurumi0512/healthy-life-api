package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.User;

//操作 User（使用者基本資料）資料表的 Repository 介面
public interface UserRepository extends JpaRepository<User, Integer> {

	// 根據帳號 ID 查詢對應的使用者（回傳 Optional）
	Optional<User> findByAccount_Id(Integer accountId);
}