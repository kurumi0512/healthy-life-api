package com.example.demo.model.entity;

import java.time.LocalDateTime;

import com.example.demo.model.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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

	@Column(nullable = false)
	private Boolean completed;

	@Column(name = "create_time")
	private LocalDateTime createTime;

	@Column(name = "last_login")
	private LocalDateTime lastLogin;

	@Column(name = "status")
	private String status;

	@Enumerated(EnumType.STRING) // 存成 USER / ADMIN 字串
	@Column(name = "role", nullable = false)
	private Role role;

	@PrePersist
	public void onCreate() {
		this.createTime = LocalDateTime.now();
	}
}