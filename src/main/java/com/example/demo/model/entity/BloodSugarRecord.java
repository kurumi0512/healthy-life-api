package com.example.demo.model.entity;

import java.time.LocalDate;

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
@Table(name = "blood_sugar_record")
@Data
public class BloodSugarRecord {

	@Id // 主鍵
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自動遞增
	private Integer id;

	@ManyToOne // 多筆血糖紀錄對應一位使用者
	@JoinColumn(name = "user_id") // 外鍵欄位為 user_id
	private User user;

	private Double fasting; // 空腹血糖值

	private Double postMeal; // 餐後血糖值

	@Column(name = "record_date") // 血糖量測日期
	private LocalDate recordDate;

	private String notes; // 備註欄（可選填）
}
