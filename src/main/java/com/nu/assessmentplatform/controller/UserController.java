package com.nu.assessmentplatform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nu.assessmentplatform.domain.Users;
import com.nu.assessmentplatform.dto.UserDTO;
import com.nu.assessmentplatform.dto.request.GoogleSignInRequest;
import com.nu.assessmentplatform.dto.response.GoogleSignInResponse;
import com.nu.assessmentplatform.dto.response.ResponseDTO;
import com.nu.assessmentplatform.service.UserService;

@RestController
@CrossOrigin("*")
@RequestMapping("/v1/user")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/google-sign-in")
	public ResponseEntity<ResponseDTO<GoogleSignInResponse>> googleSignIn(
			@RequestBody GoogleSignInRequest googleSignInRequest) {
		ResponseDTO<GoogleSignInResponse> responseDTO = userService.signInWithGoogle(googleSignInRequest);
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/get-user")
	public ResponseEntity<ResponseDTO<Users>> getUserDetail(
			@RequestParam(name = "userId", required = false) String userId,
			@RequestParam(name = "email", required = false) String email) {
		ResponseDTO<Users> responseDTO = userService.getSingleUser(userId, email);
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/create-user")
	public ResponseEntity<ResponseDTO<UserDTO>> createUser(@RequestBody Users users) {
		ResponseDTO<UserDTO> responseDTO = userService.createUser(users);
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/fetch-emails")
	public ResponseEntity<ResponseDTO<List<String>>> fetchUsersEmail(@RequestParam("prefix") String emailPrefix) {
		ResponseDTO<List<String>> responseDTO = userService.fetchEmails(emailPrefix);
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}

}
