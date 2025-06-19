package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;

public interface UserService {

	// 取得目前登入中的使用者（User 實體）
	User getCurrentLoginUser();

	// 查詢所有使用者（後台使用），回傳轉換後的 DTO 列表（不直接回傳 Entity）
	List<UserDto> findAllUsers();

	// 根據使用者 ID 查詢單一使用者，回傳轉換過的 DTO
	UserDto findUserById(Integer id);

	// 更新使用者資料（包含姓名、Email、狀態與角色）
	void updateUser(Integer id, UserDto userDto);

	// 刪除使用者（實務上多為改狀態為 INACTIVE 而非物理刪除）
	void deleteUser(Integer id);

	// 根據帳號 ID 查找對應的使用者資料，回傳轉換後的 DTO
	UserDto findByAccountId(Integer accountId);
}