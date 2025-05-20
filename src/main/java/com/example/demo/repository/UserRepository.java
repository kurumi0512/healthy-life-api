package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	User findByAccount(Account account); // ✅ 根據帳號查使用者
}