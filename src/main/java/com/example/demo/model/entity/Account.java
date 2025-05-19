package com.example.demo.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String username;

	@Column(name = "hash_password")
	private String hashPassword;

	@Column(name = "hash_salt")
	private String hashSalt;

	private String email;
	private Boolean completed;

	@Column(name = "create_time")
	private LocalDateTime createTime;

	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	private String status;

	// getter / setter 省略，可用 Lombok 處理

}
