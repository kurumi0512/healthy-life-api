package com.example.demo.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//為了放在list中,所以要包裝起來
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bmi {
	private Double height;
	private Double weight;
	private Double bmi;

	public static void main(String[] args) {
	}

}