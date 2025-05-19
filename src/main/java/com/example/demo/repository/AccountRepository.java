package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {
	Optional<Account> findByUsername(String username);
}
