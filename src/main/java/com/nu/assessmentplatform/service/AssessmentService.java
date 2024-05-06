package com.nu.assessmentplatform.service;

import org.springframework.stereotype.Service;

import com.nu.assessmentplatform.domain.AssessmentQuestions;
import com.nu.assessmentplatform.domain.Domains;
import com.nu.assessmentplatform.domain.TestStatistics;
import com.nu.assessmentplatform.dto.DomainData;
import com.nu.assessmentplatform.dto.Questions;
import com.nu.assessmentplatform.dto.response.ResponseDTO;
import com.nu.assessmentplatform.enums.Levels;

import jakarta.mail.MessagingException;

@Service
public interface AssessmentService {

	ResponseDTO<DomainData> fetchAllDomains();

	ResponseDTO<DomainData> fetchAllLevels(String domain);

	ResponseDTO<Questions> fetchQuestions(String domainName, Levels difficultyLevel, String questionCode);

	ResponseDTO<?> createDomains(Domains domains);

	ResponseDTO<?> createAssesmentQuestions(AssessmentQuestions assessmentQuestions);

	ResponseDTO<TestStatistics> getStaticsData(String domain, Levels level);

	ResponseDTO<?> assignTask(String userEmail, String questionCode) throws MessagingException;

}
