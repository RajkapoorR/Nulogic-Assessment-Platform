package com.nu.assessmentplatform.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class GoogleSignInResponse {

	private String email;

	private String firstName;

	private String lastName;

	private String displayName;

	private String googleId;

	private String token;
}
