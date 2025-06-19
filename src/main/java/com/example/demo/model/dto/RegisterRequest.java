package com.example.demo.model.dto;

// 自訂驗證註解
import com.example.demo.validator.PasswordsMatch;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 使用者註冊請求資料物件，含基本欄位與驗證規則
@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordsMatch(message = "兩次輸入的密碼不一致")
public class RegisterRequest {

	@NotEmpty(message = "使用者名稱不能為空")
	@Size(min = 3, max = 20, message = "使用者名稱長度需介於 3~20 字之間")
	private String username;

	@NotEmpty(message = "密碼不能為空")
	@Size(min = 6, max = 30, message = "密碼長度需至少 6 個字")
	private String password;

	@NotEmpty(message = "請再次輸入密碼")
	private String confirmPassword;

	@NotEmpty(message = "信箱不能為空")
	@Email(message = "信箱格式不正確")
	private String email;
}
