package com.example.demo.model.entity;

import java.time.LocalDateTime;

// ✅ 匯入 JPA 的 Id、Entity、Column 等註解
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
@Table(name = "advice_history")
@Data
public class AdviceHistory {

	@Id // 主鍵
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 資料庫自動遞增
	private Integer id;

	@ManyToOne // 多筆建議紀錄對應一位使用者
	@JoinColumn(name = "user_id") // 關聯的外鍵欄位為 user_id
	private User user;

	private String type; // 建議類型（例如：飲食、運動）

	@Column(columnDefinition = "MEDIUMTEXT") // 較長的輸入內容
	private String inputContext;

	@Column(name = "generated_advice", columnDefinition = "MEDIUMTEXT") // 較長的 AI 回覆
	private String generatedAdvice;

	@Column(name = "created_at")
	@org.hibernate.annotations.CreationTimestamp // 自動填入建立時間
	private LocalDateTime createdAt;

	@ManyToOne // 多筆建議可以對應到同一筆體重紀錄（選填）
	@JoinColumn(name = "weight_record_id")
	private WeightRecord weightRecord;
}