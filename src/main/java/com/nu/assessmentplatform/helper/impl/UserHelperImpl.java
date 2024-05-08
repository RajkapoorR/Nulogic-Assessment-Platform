package com.nu.assessmentplatform.helper.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.nu.assessmentplatform.constants.UserConstants;
import com.nu.assessmentplatform.domain.Users;
import com.nu.assessmentplatform.dto.request.GoogleSignInRequest;
import com.nu.assessmentplatform.dto.response.GoogleSignInResponse;
import com.nu.assessmentplatform.helper.UserHelper;
import com.nu.assessmentplatform.repo.UsersRepo;

@Component
public class UserHelperImpl implements UserHelper {

	@Autowired
	private UsersRepo usersRepo;

	public String hashPassword(String password) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.encode(password);
	}

	@Override
	public Users getUserByEmail(String email) {
		return usersRepo.findByEmail(email);
	}

	@Override
	public Users createUserFromRequest(GoogleSignInRequest googleSignInRequest) {
		String displayName = googleSignInRequest.getFirstName().concat(UserConstants.EMPTY)
				.concat(googleSignInRequest.getLastName());
		Users userData = new Users();
		userData.setEmail(googleSignInRequest.getEmail());
		userData.setFirstName(googleSignInRequest.getFirstName());
		userData.setLastName(googleSignInRequest.getLastName());
		userData.setGoogleAuthId(googleSignInRequest.getGoogleId());
		userData.setDisplayName(displayName);
		LocalDateTime now = LocalDateTime.now();
		String timestampString = now.format(DateTimeFormatter.ISO_DATE_TIME);
		userData.setCreatedAt(timestampString);
		userData.setUpdatedAt(timestampString);
		String hashedPassword = hashPassword(UserConstants.DEFAULT_PASSWORD);
		userData.setPassword(hashedPassword);
		usersRepo.save(userData);
		return userData;
	}

	@Override
	public GoogleSignInResponse populateGoogleSignInResponse(String token, Users users) {
		GoogleSignInResponse signInResponse = new GoogleSignInResponse();
		signInResponse.setToken(token);
		signInResponse.setEmail(users.getEmail());
		signInResponse.setDisplayName(users.getDisplayName());
		signInResponse.setGoogleId(users.getGoogleAuthId());
		signInResponse.setFirstName(users.getFirstName());
		signInResponse.setLastName(users.getLastName());
		return signInResponse;
	}

	@Override
	public Users fetchSingleUser(String userId) {
		return !usersRepo.findById(userId).isEmpty() ? usersRepo.findById(userId).get() : null;
	}

}
