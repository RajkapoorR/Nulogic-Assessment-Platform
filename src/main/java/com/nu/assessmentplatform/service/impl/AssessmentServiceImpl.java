package com.nu.assessmentplatform.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.nu.assessmentplatform.domain.AssessmentDetails;
import com.nu.assessmentplatform.domain.AssessmentQuestions;
import com.nu.assessmentplatform.domain.Domains;
import com.nu.assessmentplatform.domain.SubmissionRequest;
import com.nu.assessmentplatform.domain.TestStatistics;
import com.nu.assessmentplatform.domain.Users;
import com.nu.assessmentplatform.dto.DomainData;
import com.nu.assessmentplatform.dto.Questions;
import com.nu.assessmentplatform.dto.QuestionsDetails;
import com.nu.assessmentplatform.dto.response.ResponseDTO;
import com.nu.assessmentplatform.enums.Levels;
import com.nu.assessmentplatform.helper.UserHelper;
import com.nu.assessmentplatform.repo.AssessmentDetailsRepo;
import com.nu.assessmentplatform.repo.AssessmentQuestionsRepo;
import com.nu.assessmentplatform.repo.DomainsRepo;
import com.nu.assessmentplatform.repo.TestStatisticsRepo;
import com.nu.assessmentplatform.service.AssessmentService;
import com.nu.assessmentplatform.utils.EmailUtils;

import jakarta.mail.MessagingException;
import lombok.Data;

@Service
public class AssessmentServiceImpl implements AssessmentService {

	@Autowired
	private DomainsRepo domainsRepo;

	@Autowired
	private AssessmentDetailsRepo assessmentDetailsRepo;

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Autowired
	private UserHelper userHelper;

	@Autowired
	private AssessmentQuestionsRepo assessmentQuestionsRepo;

	@Autowired
	private EmailUtils emailUtils;

	@Autowired
	private TestStatisticsRepo testStatisticsRepo;
	@Autowired
	private AssessmentService assessmentService;

	@Override
	public ResponseDTO<DomainData> fetchAllDomains() {
		ResponseDTO<DomainData> responseDTO = new ResponseDTO<>();
		DomainData data = new DomainData();
		List<String> domainList = new ArrayList<>();
		List<Domains> allDomainData = domainsRepo.findAll();
		allDomainData.stream().forEach(x -> domainList.add(x.getName()));
		data.setDomains(domainList);
		responseDTO.setState(data);
		responseDTO.setSuccess(Boolean.TRUE);
		return responseDTO;
	}

	@Override
	public ResponseDTO<?> createDomains(Domains domains) {
		ResponseDTO<?> responseDTO = new ResponseDTO<>();
		try {
			Domains savedDomain = domainsRepo.save(domains);
			if (savedDomain != null) {
				responseDTO.setStatus("Domains created successfully");
				responseDTO.setSuccess(Boolean.TRUE);
			} else {
				responseDTO.setSuccess(Boolean.FALSE);
				responseDTO.setStatus("Issue while creating a domain");
			}
		} catch (Exception e) {
			responseDTO.setSuccess(Boolean.FALSE);
			responseDTO.setErrors("Issue occurred-" + e.getMessage());
		}
		return responseDTO;
	}

	@Override
	public ResponseDTO<?> createAssesmentQuestions(AssessmentQuestions assessmentQuestions) {
		ResponseDTO<?> responseDTO = new ResponseDTO<>();
		int questionSize = !CollectionUtils.isEmpty(assessmentQuestions.getQuestionList())
				? assessmentQuestions.getQuestionList().size()
				: 0;
		assessmentQuestions.setQuestionCount(questionSize);
		AssessmentQuestions questions = assessmentQuestionsRepo.save(assessmentQuestions);
		if (questions != null) {
			responseDTO.setStatus("Questions created successfully");
		}
		responseDTO.setSuccess(Boolean.TRUE);
		return responseDTO;
	}

	@Override
	public ResponseDTO<DomainData> fetchAllLevels(String domain) {
		ResponseDTO<DomainData> responseDTO = new ResponseDTO<>();
		DomainData data = new DomainData();
		List<Domains> allDomainData = domainsRepo.findAll();
		allDomainData.stream().forEach(x -> data.setDifficultyLevels(x.getLevels()));
		responseDTO.setState(data);
		responseDTO.setSuccess(Boolean.TRUE);
		return responseDTO;
	}

	@Override
	public ResponseDTO<Questions> fetchQuestions(String domainName, Levels difficultyLevel, String questionCode) {
		ResponseDTO<Questions> responseDTO = new ResponseDTO<>();
		Questions questions = new Questions();
		AssessmentQuestions assessmentQuestions = null;
		List<QuestionsDetails> questionsDetails = new ArrayList<>();
		if (StringUtils.hasText(questionCode)) {
			assessmentQuestions = assessmentQuestionsRepo.findByQuestionCode(questionCode);
			questions.setQuestionList(assessmentQuestions.getQuestionList());
			questions.setQuestionCount(assessmentQuestions.getQuestionList().size());

		} else {
			List<AssessmentQuestions> assessmentQuestionList = assessmentQuestionsRepo
					.findByDomainNameAndDifficultyLevel(domainName, difficultyLevel);
			if (assessmentQuestionList != null) {
				assessmentQuestionList.forEach(x -> questionsDetails.addAll(x.getQuestionList()));
			}
			questions.setQuestionList(questionsDetails);
			questions.setQuestionCount(questionsDetails.size());
		}
		responseDTO.setState(questions);
		responseDTO.setSuccess(Boolean.TRUE);
		return responseDTO;
	}

	@Override
	public ResponseDTO<TestStatistics> getStaticsData(String domain, Levels level) {
		ResponseDTO<TestStatistics> responseDTO = new ResponseDTO<>();
		TestStatistics existingTestStatics = testStatisticsRepo.findByDomainNameAndLevel(domain, level);
		if (existingTestStatics != null) {
			responseDTO.setSuccess(Boolean.TRUE);
			responseDTO.setState(existingTestStatics);
		}
		if(existingTestStatics==null) {
			responseDTO.setSuccess(Boolean.TRUE);
			responseDTO.setState(new TestStatistics());
		}
		return responseDTO;
	}

	@Override
	public ResponseDTO<?> assignTask(String userEmail, String questionCode) throws MessagingException {
		ResponseDTO<?> responseDTO = new ResponseDTO<>();
		try {
			Users userByEmail = userHelper.getUserByEmail(userEmail);
			AssessmentQuestions questions = assessmentQuestionsRepo.findByQuestionCode(questionCode);
			if (userByEmail != null && questions != null) {
				saveAssessmentDetailsToDB(questionCode, userByEmail, questions);
				generateAssessmentNotification(userEmail);
				responseDTO.setSuccess(Boolean.TRUE);
				responseDTO.setStatus("Asssesment assigned successfully and notified to the employee via mail");
			}
			if (userByEmail == null) {
				throw new IllegalAccessException("User not found");
			}
		} catch (Exception e) {
			responseDTO.setSuccess(Boolean.FALSE);
			responseDTO.setErrors("Issue while assigning the assessment - Cause - " + e.getMessage());

		}
		return responseDTO;
	}

	private void generateAssessmentNotification(String userEmail) throws MessagingException {
		Context context = new Context();
		String html = templateEngine.process("assessment-notification-template", context);
		emailUtils.sendEmail(userEmail, "New Assessment Assigned for You", html);
	}

	private void saveAssessmentDetailsToDB(String questionCode, Users userByEmail, AssessmentQuestions questions) {
		AssessmentDetails assessmentDetails = new AssessmentDetails();
		assessmentDetails.setUser(userByEmail);
		assessmentDetails.setQuestionCode(questionCode);
		assessmentDetails.setDomain(questions.getDomainName());
		assessmentDetails.setLevel(questions.getDifficultyLevel());
		assessmentDetails.setQuestionCount(questions.getQuestionCount());
		assessmentDetailsRepo.save(assessmentDetails);
	}

	@Override
	public ResponseDTO<?> submitAnswer(SubmissionRequest submissionRequest) {
		try {
			int selectedOption = submissionRequest.getSelectedOption();
			String assessmentId = submissionRequest.getAssessmentId();

			// Call the method to update assessment details based on user submission
			AssessmentDetails updatedAssessment = updateAssessmentDetails(assessmentId, selectedOption);

			// Prepare a success response
			ResponseDTO<AssessmentDetails> responseDTO = new ResponseDTO<>();
			responseDTO.setSuccess(true);
			responseDTO.setMessage("Answer submitted successfully");
			responseDTO.setData(updatedAssessment);

			return responseDTO;
		} catch (Exception e) {
			// Handle any exceptions and prepare an error response
			return ResponseDTO.error("Failed to submit answer: " + e.getMessage());
		}
	}

	@Override
	public void save(AssessmentDetails assessmentDetails) {

	}

	@Override
	public AssessmentDetails getAssessmentDetailsById(String assessmentId) {
		return null;
	}
	@Override
	public AssessmentDetails updateAssessmentDetails(String assessmentId, int selectedOption) {
		AssessmentDetails assessmentDetails = assessmentService.getAssessmentDetailsById(assessmentId);
		assessmentDetails.setSelectedOption(selectedOption);
		assessmentService.save(assessmentDetails);
		return assessmentDetails;
	}

}
