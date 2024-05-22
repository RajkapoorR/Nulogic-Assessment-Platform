package com.nu.assessmentplatform.helper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.nu.assessmentplatform.domain.Users;
import com.nu.assessmentplatform.dto.UserDTO;
import com.nu.assessmentplatform.dto.request.GoogleSignInRequest;
import com.nu.assessmentplatform.dto.response.GoogleSignInResponse;

@Component
public interface UserHelper {

	Users getUserByEmail(String email);

	Users createUserFromRequest(GoogleSignInRequest googleSignInRequest);

	GoogleSignInResponse populateGoogleSignInResponse(String token, Users users);

	Users fetchSingleUser(String userId);

	Users fetchUserByEmail(String email);

	Users saveUserDataToDB(Users users);

	UserDTO populateUserResponse(Users savedUser);

	List<String> findEmailsByPrefix(String prefix);

}
