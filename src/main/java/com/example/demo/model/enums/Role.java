package com.example.demo.model.enums;

/**
 * 使用者角色列舉 - USER：一般使用者 - ADMIN：系統管理員
 */
public enum Role {
	USER, // 一般使用者，權限較低，只能操作自己相關資料
	ADMIN // 管理員，擁有後台與所有使用者資料的管理權限
}