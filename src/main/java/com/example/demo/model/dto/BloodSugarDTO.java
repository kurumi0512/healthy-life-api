package com.example.demo.model.dto;

import java.time.LocalDate;

public class BloodSugarDTO {
	private Integer id; // 可選：查詢時顯示用
	private Double fasting; // 空腹血糖
	private Double postMeal; // 飯後血糖
	private LocalDate recordDate; // 記錄日期
	private String notes; // 備註（可為 null）

	// Getter & Setter
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getFasting() {
		return fasting;
	}

	public void setFasting(Double fasting) {
		this.fasting = fasting;
	}

	public Double getPostMeal() {
		return postMeal;
	}

	public void setPostMeal(Double postMeal) {
		this.postMeal = postMeal;
	}

	public LocalDate getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(LocalDate recordDate) {
		this.recordDate = recordDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
