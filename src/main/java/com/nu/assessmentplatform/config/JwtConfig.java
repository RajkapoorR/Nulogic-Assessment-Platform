package com.nu.assessmentplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@Data
public class JwtConfig {

	@Value("${jwt.expires-in}")
	private long expiresIn;

}
