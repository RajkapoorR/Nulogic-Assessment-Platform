package com.nu.assessmentplatform.controller;

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

import com.nu.assessmentplatform.domain.AssessmentDetails;
import com.nu.assessmentplatform.domain.AssessmentQuestions;
import com.nu.assessmentplatform.domain.Domains;
import com.nu.assessmentplatform.domain.SubmissionRequest;
import com.nu.assessmentplatform.domain.TestStatistics;
import com.nu.assessmentplatform.dto.DomainData;
import com.nu.assessmentplatform.dto.Questions;
import com.nu.assessmentplatform.dto.response.ResponseDTO;
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

	@PostMapping("/create-domain")
	public ResponseEntity<ResponseDTO<?>> createDomain(@RequestBody Domains domains) {
		ResponseDTO<?> responseDTO = assessmentService.createDomains(domains);
		return ResponseEntity.ok(responseDTO);
	}

	@PostMapping("/create-questions")
	public ResponseEntity<ResponseDTO<?>> createAssessmentQuestions(
			@RequestBody AssessmentQuestions assessmentQuestions) {
		ResponseDTO<?> responseDTO = assessmentService.createAssesmentQuestions(assessmentQuestions);
		return ResponseEntity.ok(responseDTO);
	}

	@GetMapping("/levels/{domain}")
	public ResponseEntity<ResponseDTO<DomainData>> getDomainLevels(@PathVariable("domain") String domain) {
		ResponseDTO<DomainData> responseDTO = assessmentService.fetchAllLevels(domain);
		return ResponseEntity.ok(responseDTO);
	}

	@GetMapping("/questions")
	public ResponseEntity<ResponseDTO<Questions>> getQuestions(
			@RequestParam(name = "domain", required = false) String domainName,
			@RequestParam(name = "difficultyLevel", required = false) Levels difficultyLevel,
			@RequestParam(name = "questionCode", required = false) String questionCode) {
		ResponseDTO<Questions> responseDTO = assessmentService.fetchQuestions(domainName, difficultyLevel,
				questionCode);
		return ResponseEntity.ok(responseDTO);
	}

	@GetMapping("/statistics")
	public ResponseEntity<ResponseDTO<TestStatistics>> getTestStatistics(
			@RequestParam(name = "domain") String domainName,
			@RequestParam(name = "difficultyLevel") Levels difficultyLevel) {
		ResponseDTO<TestStatistics> responseDTO = assessmentService.getStaticsData(domainName, difficultyLevel);
		return ResponseEntity.ok(responseDTO);
	}

	@GetMapping("/assignTask")
	public ResponseDTO<?> assignTask(@RequestParam("userEmail") String userEmail,
			@RequestParam("questionCode") String questionCode) throws MessagingException {
		ResponseDTO<?> assignTask = assessmentService.assignTask(userEmail, questionCode);
		return assignTask;
	}

	// Endpoint for submitting user answer
	@PostMapping("/submit-answer")
	public AssessmentDetails submitAnswer(@RequestBody SubmissionRequest submissionRequest) {
		int selectedOption = submissionRequest.getSelectedOption();
		String assessmentId = submissionRequest.getAssessmentId();

		// Call service layer to update assessment details based on user submission
		return assessmentService.updateAssessmentDetails(assessmentId, selectedOption);
	}
}
