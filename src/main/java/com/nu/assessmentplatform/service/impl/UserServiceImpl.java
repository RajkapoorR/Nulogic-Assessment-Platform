package com.nu.assessmentplatform.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nu.assessmentplatform.constants.UserConstants;
import com.nu.assessmentplatform.domain.Users;
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
			} else {
				Users userData = userHelper.createUserFromRequest(googleSignInRequest);
				token = jwtUtil.generateToken(users);
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
}
