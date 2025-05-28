package com.example.demo.mapper;

import com.example.demo.model.dto.AccountResponseDTO;
import com.example.demo.model.entity.Account;

public class AccountMapper {

	public static AccountResponseDTO toResponseDTO(Account account) {
		if (account == null)
			return null;

		AccountResponseDTO dto = new AccountResponseDTO();
		dto.setId(account.getId());
		dto.setUsername(account.getUsername());
		dto.setEmail(account.getEmail());
		dto.setCompleted(account.getCompleted());
		dto.setCreateTime(account.getCreateTime());
		dto.setLastLogin(account.getLastLogin());
		dto.setStatus(account.getStatus());
		dto.setRole(account.getRole());
		return dto;
	}
}