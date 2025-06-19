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
@Table(name = "account") // 對應資料表 account
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自動編號主鍵
	private Integer id;

	private String username; // 帳號名稱（唯一，建議配合唯一索引）

	@Column(name = "hash_password")
	private String hashPassword; // 雜湊後密碼

	@Column(name = "hash_salt")
	private String hashSalt; // 對應密碼的鹽值

	private String email; // 信箱（建議加唯一索引）

	@Column(nullable = false)
	private Boolean completed; // 是否完成個人資料設定（ex: 第一次登入引導）

	@Column(name = "create_time")
	private LocalDateTime createTime; // 建立時間，自動在註冊時寫入

	@Column(name = "last_login")
	private LocalDateTime lastLogin; // 最近登入時間

	@Column(name = "status")
	private String status; // 帳號狀態（建議用 active / inactive / banned）

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role role; // 帳號角色（USER / ADMIN）

	@Column(name = "reset_secret")
	private String resetSecret; // 密碼重設驗證用的一次性字串

	@PrePersist
	public void onCreate() {
		this.createTime = LocalDateTime.now(); // 建立時自動填入時間
	}
}