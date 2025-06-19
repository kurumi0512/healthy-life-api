package com.example.demo.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@EntityListeners(AuditingEntityListener.class) // 加上監聽器
@Table(name = "weight_record")
@Data
public class WeightRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 主鍵自動遞增
	private Integer id;

	@ManyToOne // 多筆體重紀錄對應一位使用者
	@JoinColumn(name = "user_id") // 外鍵欄位 user_id
	private User user;

	private Double weight; // 體重（公斤）
	private Double height; // 身高（公分）
	private Integer age; // 當時紀錄的年齡
	private Double bmi; // 計算出的 BMI 值

	@Column(name = "record_date") // 使用者實際輸入的量測日期
	private LocalDate recordDate;

	@Column(name = "created_at", updatable = false) // 建立時間，由系統填入，不允許更新
	@CreatedDate
	private LocalDateTime createdAt;

	@Column(name = "updated_at") // 每次更新這筆紀錄時自動更新
	@LastModifiedDate
	private LocalDateTime updatedAt;
}
