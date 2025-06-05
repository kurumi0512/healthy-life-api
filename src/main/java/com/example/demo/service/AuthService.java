package com.example.demo.service;

import com.example.demo.exception.CertException;
import com.example.demo.model.dto.UserCert;

public interface AuthService {
	UserCert validate(String username, String rawPassword) throws CertException;
}
