package com.nu.assessmentplatform.dto.request;

import lombok.Data;

@Data
public class GoogleSignInRequest {

	private String firstName;
	private String lastName;
	private String email;
	private String googleId;
}
