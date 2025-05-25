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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	private Integer systolic;

	private Integer diastolic;

	@Column(name = "record_date")
	private LocalDate recordDate; // 使用者輸入的血壓測量日期

	private String notes; // 備註欄（可選填）

	@Column(name = "created_at", updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt; // 系統自動記錄建立時間
}