package com.nu.assessmentplatform.helper;

import org.springframework.stereotype.Component;

import com.nu.assessmentplatform.domain.Users;
import com.nu.assessmentplatform.dto.request.GoogleSignInRequest;
import com.nu.assessmentplatform.dto.response.GoogleSignInResponse;

@Component
public interface UserHelper {

	Users getUserByEmail(String email);

	Users createUserFromRequest(GoogleSignInRequest googleSignInRequest);

	GoogleSignInResponse populateGoogleSignInResponse(String token, Users users);

	Users fetchSingleUser(String userId);


}
