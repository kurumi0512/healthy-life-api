package com.example.demo.validator;

import com.example.demo.model.dto.RegisterRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, RegisterRequest> {

	@Override
	public boolean isValid(RegisterRequest request, ConstraintValidatorContext context) {
		if (request.getPassword() == null || request.getConfirmPassword() == null) {
			return false;
		}
		return request.getPassword().equals(request.getConfirmPassword());
	}
}