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
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 使用「資料庫的自動遞增 ID」方式來產生主鍵值
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	private Double weight;
	private Double height;
	private Integer age;
	private Double bmi;

	@Column(name = "record_date") // 使用者實際記錄的測量日期,使用者輸入
	private LocalDate recordDate;

	@Column(name = "created_at", updatable = false) // 資料寫入資料庫的「建立時間」,系統建立
	@CreatedDate
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	@LastModifiedDate
	private LocalDateTime updatedAt; // 每次更新時自動更新
}
