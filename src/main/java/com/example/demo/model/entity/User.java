package com.example.demo.model.entity;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自動生成id
	private Integer id;

	@OneToOne
	@JoinColumn(name = "account_id")
	private Account account;

	@Column(name = "name")
	private String name;

	@Column(name = "age")
	private Integer age;

	@Column(name = "gender")
	private String gender;

	@Column(name = "goal")
	private String goal;

	@Column(name = "height")
	private Double height;

	@Column(name = "target_Weight")
	private Double targetWeight;

	@Column(name = "age_group")
	private String ageGroup;

	@Column(name = "create_time")
	private LocalDateTime createTime;

	public Integer getAccountId() {
		return account != null ? account.getId() : null;
	}
}
