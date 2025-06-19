package com.example.demo.service;

import com.example.demo.model.dto.UserProfileDto;

//使用者個人資料的服務介面
public interface UserProfileService {

	// 取得指定帳號的個人資料（包含年齡、性別、身高、目標體重等）
	UserProfileDto getProfile(Integer accountId);

	// 更新指定帳號的個人資料（從前端送入 DTO 資料進行儲存）
	void updateProfile(Integer accountId, UserProfileDto dto);
}
