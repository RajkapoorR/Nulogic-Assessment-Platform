package com.nu.assessmentplatform.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nu.assessmentplatform.domain.Users;
import com.nu.assessmentplatform.dto.UserDTO;
import com.nu.assessmentplatform.dto.request.GoogleSignInRequest;
import com.nu.assessmentplatform.dto.response.GoogleSignInResponse;
import com.nu.assessmentplatform.dto.response.ResponseDTO;

@Service
public interface UserService {

	ResponseDTO<GoogleSignInResponse> signInWithGoogle(GoogleSignInRequest googleSignInRequest);

	ResponseDTO<Users> getSingleUser(String userId, String email);

	ResponseDTO<UserDTO> createUser(Users users);

	ResponseDTO<List<String>> fetchEmails(String prefix);

}
