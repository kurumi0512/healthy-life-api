package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;

public interface UserService {
	User getCurrentLoginUser(); // 已有的

	List<UserDto> findAllUsers(); // 後台查詢所有使用者

	UserDto findUserById(Integer id); // 查詢單一使用者

	void updateUser(Integer id, UserDto userDto); // 修改使用者資料

	void deleteUser(Integer id); // 刪除使用者（或設 active = false）

	UserDto findByAccountId(Integer accountId);
}