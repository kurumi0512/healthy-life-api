package com.example.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "food")
public class Food {

	@Id // 主鍵
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 資料庫自動遞增
	private Integer id;

	private String name; // 食物名稱，例如：雞胸肉、豆腐

	private String category; // 分類，例如：肉類、豆製品、蔬菜

	@Column(name = "protein_per_100g") // 每 100 克含有多少蛋白質
	private Double proteinPer100g;

	private Double calories; // 每 100 克的熱量（單位：卡）

	@Column(name = "is_vegan") // 是否為純素食（true/false）
	private Boolean isVegan;

	private String note; // 備註
}
