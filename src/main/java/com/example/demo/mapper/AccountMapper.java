package com.example.demo.mapper;

import com.example.demo.model.dto.AccountResponseDTO;
import com.example.demo.model.entity.Account;

// 資料轉換工具類（Entity → DTO）
public class AccountMapper {

	// 將 Account 實體轉成 AccountResponseDTO（給前端用）
	public static AccountResponseDTO toResponseDTO(Account account) {
		if (account == null)
			return null; // 若為空，直接回傳 null

		// 手動把欄位一一複製過去（只選擇前端需要的欄位）
		AccountResponseDTO dto = new AccountResponseDTO();
		dto.setId(account.getId());
		dto.setUsername(account.getUsername());
		dto.setEmail(account.getEmail());
		dto.setCompleted(account.getCompleted());
		dto.setCreateTime(account.getCreateTime());
		dto.setLastLogin(account.getLastLogin());
		dto.setStatus(account.getStatus());
		dto.setRole(account.getRole());
		return dto; // 回傳 DTO 給 Controller 或 Service 使用
	}
}