package com.example.demo.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 主鍵自動遞增
	private Integer id;

	@OneToOne // 每位使用者對應一個帳號
	@JoinColumn(name = "account_id") // 外鍵欄位為 account_id
	private Account account;

	@Column(name = "name") // 使用者名稱
	private String name;

	@Column(name = "birth_date") // 生日（可用於計算年齡）
	private LocalDate birthDate;

	@Column(name = "gender") // 性別（例如 "男"、"女"、"其他"）
	private String gender;

	@Column(name = "goal") // 健康目標（如：減重、增肌、維持健康）
	private String goal;

	@Column(name = "height") // 身高（公分）
	private Double height;

	@Column(name = "target_Weight") // 目標體重（公斤）
	private Double targetWeight;

	@Column(name = "age_group") // 年齡層（如：青少年、成年人、老年人）
	private String ageGroup;

	@Column(name = "create_time") // 記錄使用者建立時間
	private LocalDateTime createTime;

	// 取出關聯帳號的 id（避免直接暴露整個帳號物件）
	public Integer getAccountId() {
		return account != null ? account.getId() : null;
	}
}
