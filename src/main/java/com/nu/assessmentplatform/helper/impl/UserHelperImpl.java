package com.nu.assessmentplatform.helper.impl;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.nu.assessmentplatform.constants.UserConstants;
import com.nu.assessmentplatform.domain.Users;
import com.nu.assessmentplatform.dto.UserDTO;
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
		userData.setUserRole("enduser");
		Date now = Date.from(Instant.now());
		userData.setCreatedAt(now);
		userData.setUpdatedAt(now);
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
		signInResponse.setUserRole(users.getUserRole());
		return signInResponse;
	}

	@Override
	public Users fetchSingleUser(String userId) {
		return !usersRepo.findById(userId).isEmpty() ? usersRepo.findById(userId).get() : null;
	}

	@Override
	public Users fetchUserByEmail(String email) {
		return usersRepo.findByEmail(email) != null ? usersRepo.findByEmail(email) : null;
	}

	@Override
	public Users saveUserDataToDB(Users users) {
		Date now = Date.from(Instant.now());
		users.setCreatedAt(now);
		users.setUpdatedAt(now);
		String hashedPassword = hashPassword(users.getPassword());
		users.setPassword(hashedPassword);
		Users saveUser = usersRepo.save(users);
		return saveUser;
	}

	@Override
	public UserDTO populateUserResponse(Users savedUser) {
		UserDTO userDTO = new UserDTO();
		userDTO.setDisplayName(savedUser.getDisplayName());
		userDTO.setEmail(savedUser.getEmail());
		userDTO.setFirstName(savedUser.getFirstName());
		userDTO.setId(savedUser.getId());
		userDTO.setLastName(savedUser.getLastName());
		userDTO.setUserRole(savedUser.getUserRole());
		return userDTO;
	}

}
