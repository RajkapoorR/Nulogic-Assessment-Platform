package com.nu.assessmentplatform.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nu.assessmentplatform.constants.UserConstants;
import com.nu.assessmentplatform.domain.Users;
import com.nu.assessmentplatform.dto.UserDTO;
import com.nu.assessmentplatform.dto.request.GoogleSignInRequest;
import com.nu.assessmentplatform.dto.response.GoogleSignInResponse;
import com.nu.assessmentplatform.dto.response.ResponseDTO;
import com.nu.assessmentplatform.helper.UserHelper;
import com.nu.assessmentplatform.service.UserService;
import com.nu.assessmentplatform.utils.JwtUtil;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserHelper userHelper;

	@Override
	public ResponseDTO<GoogleSignInResponse> signInWithGoogle(GoogleSignInRequest googleSignInRequest) {
		ResponseDTO<GoogleSignInResponse> responseDTO = new ResponseDTO<>();
		GoogleSignInResponse googleSignInResponse = new GoogleSignInResponse();
		try {
			String token = null;
			if (googleSignInRequest == null) {
				responseDTO.setErrors(UserConstants.EMPTY_REQUEST);
				responseDTO.setSuccess(Boolean.FALSE);
				return responseDTO;
			}
			if (!googleSignInRequest.getEmail().contains(UserConstants.NULOGIC_EMAIL)
					&& !googleSignInRequest.getEmail().contains(UserConstants.NUTECHNOLOGY_EMAIL)) {
				throw new IllegalArgumentException(UserConstants.EMAIL_VALIDATION_ERROR);
			}
			Users users = userHelper.getUserByEmail(googleSignInRequest.getEmail());
			if (users != null) {
				token = jwtUtil.generateToken(users);
				googleSignInResponse = userHelper.populateGoogleSignInResponse(token, users);
				responseDTO.setState(googleSignInResponse);
				responseDTO.setSuccess(Boolean.TRUE);
			}
			if (users == null) {
				throw new IllegalArgumentException("User can not be found..Kindly contact the user..");
			} else {
				Users userData = userHelper.createUserFromRequest(googleSignInRequest);
				token = jwtUtil.generateToken(userData);
				googleSignInResponse = userHelper.populateGoogleSignInResponse(token, userData);
				responseDTO.setState(googleSignInResponse);
				responseDTO.setSuccess(Boolean.TRUE);
			}
		} catch (Exception e) {
			responseDTO.setSuccess(Boolean.FALSE);
			responseDTO.setErrors("Failed to sign in :[ Cause - " + e.getMessage() + "]");
		}
		return responseDTO;
	}

	@Override
	public ResponseDTO<Users> getSingleUser(String userId, String email) {
		ResponseDTO<Users> responseDTO = new ResponseDTO<>();
		try {
			Users user = null;
			if (userId != null) {
				user = userHelper.fetchSingleUser(userId);

			}
			if (email != null) {
				user = userHelper.fetchUserByEmail(email);
			}
			if (user == null) {
				throw new IllegalArgumentException(UserConstants.USER_NOT_FOUND);
			}
			responseDTO.setSuccess(Boolean.TRUE);
			responseDTO.setState(user);
		} catch (Exception e) {
			responseDTO.setSuccess(Boolean.FALSE);
			responseDTO.setErrors("Failed to fetch user :[ Cause - " + e.getMessage() + "]");
		}
		return responseDTO;
	}

	@Override
	public ResponseDTO<UserDTO> createUser(Users users) {
		ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();
		UserDTO userDTO = null;
		try {
			if (users == null) {
				responseDTO.setErrors(UserConstants.EMPTY_REQUEST);
				responseDTO.setSuccess(Boolean.FALSE);
				return responseDTO;
			}
			Users existingUser = userHelper.getUserByEmail(users.getEmail());
			if (existingUser != null) {
				throw new IllegalArgumentException(UserConstants.USER_EXISTS);
			}
			Users savedUser = userHelper.saveUserDataToDB(users);
			userDTO = userHelper.populateUserResponse(savedUser);
			responseDTO.setSuccess(Boolean.TRUE);
			responseDTO.setState(userDTO);
		} catch (Exception e) {
			responseDTO.setSuccess(Boolean.FALSE);
			responseDTO.setErrors("Failed to create user :[ Cause - " + e.getMessage() + "]");
		}
		return responseDTO;
	}

	@Override
	public ResponseDTO<List<String>> fetchEmails(String prefix, String domain) {
		ResponseDTO<List<String>> responseDTO = new ResponseDTO<>();
		List<String> emails = null;
		if (prefix != null) {
			emails = userHelper.findEmailsByPrefix(prefix);
		}
		if (domain != null) {
			emails = userHelper.findEmailByDomains(domain);
		}
		responseDTO.setSuccess(Boolean.TRUE);
		responseDTO.setState(emails);
		return responseDTO;
	}
}
