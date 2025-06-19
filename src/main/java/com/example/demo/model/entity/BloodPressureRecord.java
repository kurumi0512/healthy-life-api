package com.example.demo.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "blood_pressure_record")
@Data
public class BloodPressureRecord {

	@Id // 主鍵
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自動遞增
	private Integer id;

	@ManyToOne // 多筆血壓紀錄對應一位使用者
	@JoinColumn(name = "user_id") // 對應的外鍵欄位名稱為 user_id
	private User user;

	private Integer systolic; // 收縮壓（高壓）

	private Integer diastolic; // 舒張壓（低壓）

	@Column(name = "record_date") // 紀錄實際量測日期
	private LocalDate recordDate;

	private String notes; // 備註（使用者可填寫）

	@Column(name = "created_at", updatable = false) // 建立時自動填入，不能修改
	@CreationTimestamp // 系統自動加入紀錄建立時間
	private LocalDateTime createdAt;
}