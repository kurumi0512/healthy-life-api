package com.example.demo.mapper;

import com.example.demo.model.dto.AccountResponseDTO;
import com.example.demo.model.entity.Account;

//資料轉換 的用途
public class AccountMapper {

	public static AccountResponseDTO toResponseDTO(Account account) {
		if (account == null)
			return null;

		// 將 Account 實體中對應欄位的資料，逐一複製到 AccountResponseDTO 中
		AccountResponseDTO dto = new AccountResponseDTO();
		dto.setId(account.getId());
		dto.setUsername(account.getUsername());
		dto.setEmail(account.getEmail());
		dto.setCompleted(account.getCompleted());
		dto.setCreateTime(account.getCreateTime());
		dto.setLastLogin(account.getLastLogin());
		dto.setStatus(account.getStatus());
		dto.setRole(account.getRole());
		return dto; // 回傳轉換完成的 DTO，供 Controller 或 Service 呼叫
	}
}