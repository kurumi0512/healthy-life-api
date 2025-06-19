package com.example.demo.validator;

import com.example.demo.model.dto.RegisterRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * ✅ 密碼一致性驗證器（搭配 @PasswordsMatch 註解使用） 用於驗證 RegisterRequest 中 password 與
 * confirmPassword 是否一致
 */
public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, RegisterRequest> {

	/**
	 * 核心驗證邏輯
	 * 
	 * @param request 目前正在驗證的註冊資料物件
	 * @param context 驗證上下文（可用來自定義錯誤訊息）
	 * @return 若密碼與確認密碼一致，則回傳 true；否則 false
	 */
	@Override
	public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
		// 如果任一為 null，視為不合法（避免 NullPointerException）
		if (request.getPassword() == null || request.getConfirmPassword() == null) {
			return false;
		}
		// 回傳比對結果：密碼是否一致
		return request.getPassword().equals(request.getConfirmPassword());
	}
}