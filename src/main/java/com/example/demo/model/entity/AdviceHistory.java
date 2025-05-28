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
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	private String type;

	@Column(columnDefinition = "MEDIUMTEXT")
	private String inputContext;

	@Column(name = "generated_advice", columnDefinition = "MEDIUMTEXT")
	private String generatedAdvice;

	@Column(name = "created_at")
	@org.hibernate.annotations.CreationTimestamp // ✅ 自動加上目前時間
	private LocalDateTime createdAt;

	@ManyToOne
	@JoinColumn(name = "weight_record_id")
	private WeightRecord weightRecord;
}