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

	@Id
	private Integer foodId;

	@OneToOne
	@MapsId
	@JoinColumn(name = "food_id")
	private Food food;

	@Column(name = "max_portion_g")
	private Double maxPortionG;

	private String note;
}
