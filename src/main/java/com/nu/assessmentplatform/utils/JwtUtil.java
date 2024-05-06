package com.nu.assessmentplatform.utils;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nu.assessmentplatform.config.JwtConfig;
import com.nu.assessmentplatform.constants.UserConstants;
import com.nu.assessmentplatform.domain.Users;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtil {

	@Autowired
	private JwtConfig jwtConfig;
	SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	public String generateToken(Users user) {
		long expirationTimeInMilliseconds = jwtConfig.getExpiresIn() * 1000; // Convert seconds to ms
		Date expirationDate = new Date(System.currentTimeMillis() + expirationTimeInMilliseconds);
		return Jwts.builder().setSubject(user.getEmail()).claim(UserConstants.ID, user.getId())
				.claim(UserConstants.ROLE, user.getUserRole()).setExpiration(expirationDate)
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

}
