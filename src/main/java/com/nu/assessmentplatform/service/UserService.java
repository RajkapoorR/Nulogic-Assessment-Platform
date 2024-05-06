package com.nu.assessmentplatform.service;

import org.springframework.stereotype.Service;

import com.nu.assessmentplatform.dto.request.GoogleSignInRequest;
import com.nu.assessmentplatform.dto.response.GoogleSignInResponse;
import com.nu.assessmentplatform.dto.response.ResponseDTO;

@Service
public interface UserService {

	ResponseDTO<GoogleSignInResponse> signInWithGoogle(GoogleSignInRequest googleSignInRequest);

}
