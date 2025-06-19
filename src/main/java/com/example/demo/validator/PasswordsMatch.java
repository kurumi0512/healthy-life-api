package com.example.demo.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * ✅ 自訂註解：@PasswordsMatch 用於驗證「密碼與確認密碼是否一致」的類別級別驗證（套用在 class 上） 通常搭配註冊 DTO
 * 使用，例如：RegisterRequest（含 password 與 confirmPassword）
 */
@Documented
@Constraint(validatedBy = PasswordsMatchValidator.class) // 指定驗證邏輯實作類別
@Target({ ElementType.TYPE }) // 只能套用在類別上（Class-level）
@Retention(RetentionPolicy.RUNTIME) // 在執行時期仍保留，讓 Validator 能讀取
public @interface PasswordsMatch {

	// 驗證失敗時顯示的訊息
	String message() default "密碼與確認密碼不一致";

	// 分組功能（通常用不到，保留預設即可）
	Class<?>[] groups() default {};

	// 可攜帶額外資訊的容器（通常用不到）
	Class<? extends Payload>[] payload() default {};
}
