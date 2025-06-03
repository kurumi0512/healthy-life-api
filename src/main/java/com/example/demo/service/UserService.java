package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;

public interface UserService {
	User getCurrentLoginUser(); // 取得目前登入中的使用者（User 實體）

	List<UserDto> findAllUsers(); // 後台用來查詢所有使用者，回傳 DTO 列表（不直接給 Entity）。

	UserDto findUserById(Integer id); // 根據使用者 ID 查詢單一使用者，回傳轉換過的 DTO

	void updateUser(Integer id, UserDto userDto); // 更新使用者資料，包含姓名、Email、狀態與角色

	void deleteUser(Integer id); // 刪除使用者（實務上不會真刪除，而是改成 INACTIVE 狀態）

	UserDto findByAccountId(Integer accountId); // 根據帳號 ID 查找對應的使用者資料，回傳 DTO
}