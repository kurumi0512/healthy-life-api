package com.example.demo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "food_limit")
public class FoodLimit {

	@Id // 主鍵：與 food.id 相同
	private Integer foodId;

	@OneToOne // 一對一關聯（每個 Food 對應一筆限制）
	@MapsId // 使用 food.id 當作這個 Entity 的主鍵
	@JoinColumn(name = "food_id") // 外鍵欄位名稱
	private Food food;

	@Column(name = "max_portion_g") // 單次建議攝取上限（公克）
	private Double maxPortionG;

	private String note; // 備註（例如：高蛋白不宜多食）
}
