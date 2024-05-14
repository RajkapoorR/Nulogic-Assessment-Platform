package com.nu.assessmentplatform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nu.assessmentplatform.domain.AssessmentDetails;
import com.nu.assessmentplatform.domain.AssessmentQuestions;
import com.nu.assessmentplatform.domain.Domains;
import com.nu.assessmentplatform.domain.TestStatistics;
import com.nu.assessmentplatform.dto.DomainData;
import com.nu.assessmentplatform.dto.Questions;
import com.nu.assessmentplatform.dto.request.SubmitAssessmentRequest;
import com.nu.assessmentplatform.dto.response.ResponseDTO;
import com.nu.assessmentplatform.dto.response.ScoreResponse;
import com.nu.assessmentplatform.enums.AssessmentStatus;
import com.nu.assessmentplatform.enums.Levels;
import com.nu.assessmentplatform.service.AssessmentService;

import jakarta.mail.MessagingException;

@RestController
@CrossOrigin("*")
@RequestMapping("/v1/assessment")
public class AssessmentController {
	@Autowired
	private AssessmentService assessmentService;

	@GetMapping("/domains")
	public ResponseEntity<ResponseDTO<DomainData>> getAllDomains() {
		ResponseDTO<DomainData> responseDTO = assessmentService.fetchAllDomains();
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/questionCodes")
	public ResponseEntity<ResponseDTO<DomainData>> getAllQuestionCode() {
		ResponseDTO<DomainData> responseDTO = assessmentService.fetchAllQuestionCode();
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/create-domain")
	public ResponseEntity<ResponseDTO<?>> createDomain(@RequestBody Domains domains) {
		ResponseDTO<?> responseDTO = assessmentService.createDomains(domains);
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/create-questions")
	public ResponseEntity<ResponseDTO<?>> createAssessmentQuestions(
			@RequestBody AssessmentQuestions assessmentQuestions) {
		ResponseDTO<?> responseDTO = assessmentService.createAssesmentQuestions(assessmentQuestions);
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/levels/{domain}")
	public ResponseEntity<ResponseDTO<DomainData>> getDomainLevels(@PathVariable("domain") String domain) {
		ResponseDTO<DomainData> responseDTO = assessmentService.fetchAllLevels(domain);
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/questions")
	public ResponseEntity<ResponseDTO<Questions>> getQuestions(
			@RequestParam(name = "domain", required = false) String domainName,
			@RequestParam(name = "difficultyLevel", required = false) Levels difficultyLevel,
			@RequestParam(name = "questionCode", required = false) String questionCode) {
		ResponseDTO<Questions> responseDTO = assessmentService.fetchQuestions(domainName, difficultyLevel,
				questionCode);
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/statistics")
	public ResponseEntity<ResponseDTO<List<TestStatistics>>> getTestStatistics(
			@RequestParam(name = "domain", required = false) String domainName,
			@RequestParam(name = "difficultyLevel", required = false) Levels difficultyLevel) {
		ResponseDTO<List<TestStatistics>> responseDTO = assessmentService.getStaticsData(domainName, difficultyLevel);
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/assignTask")
	public ResponseEntity<ResponseDTO<?>> assignTask(@RequestParam("userEmail") String userEmail,
			@RequestParam("questionCode") String questionCode) throws MessagingException {
		ResponseDTO<?> responseDTO = assessmentService.assignTask(userEmail, questionCode);
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/getUserScore")
	public ResponseEntity<ResponseDTO<List<AssessmentDetails>>> getScore(@RequestParam("userEmail") String userEmail) {
		ResponseDTO<List<AssessmentDetails>> responseDTO = assessmentService.getUserScore(userEmail);
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/fetchUserAssessment")
	public ResponseEntity<ResponseDTO<List<AssessmentDetails>>> getUserAssessment(
			@RequestParam("userEmail") String userEmail,
			@RequestParam(name = "assessmentStatus", required = false) AssessmentStatus assessmentStatus) {
		ResponseDTO<List<AssessmentDetails>> responseDTO = assessmentService.fetchUsersAssignedAssessment(userEmail,
				assessmentStatus);
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/submitResponse")
	public ResponseEntity<ResponseDTO<ScoreResponse>> submitResponse(
			@RequestBody SubmitAssessmentRequest assessmentRequest) {
		ResponseDTO<ScoreResponse> responseDTO = assessmentService.submitResponse(assessmentRequest);
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/upload")
	public ResponseEntity<ResponseDTO<?>> uploadQuestions(@RequestParam("file") MultipartFile file) {
		ResponseDTO<?> responseDTO = assessmentService.readAndSaveAssessmentQuestion(file);
		if (responseDTO.isSuccess()) {
			return new ResponseEntity<>(responseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
		}
	}
}
