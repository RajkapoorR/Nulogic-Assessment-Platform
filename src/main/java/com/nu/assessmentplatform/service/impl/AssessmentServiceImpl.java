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
import com.nu.assessmentplatform.domain.TestStatistics;
import com.nu.assessmentplatform.domain.Users;
import com.nu.assessmentplatform.dto.DomainData;
import com.nu.assessmentplatform.dto.Questions;
import com.nu.assessmentplatform.dto.QuestionsDetails;
import com.nu.assessmentplatform.dto.request.SubmitAssessmentRequest;
import com.nu.assessmentplatform.dto.response.ResponseDTO;
import com.nu.assessmentplatform.dto.response.ScoreResponse;
import com.nu.assessmentplatform.enums.AssessmentStatus;
import com.nu.assessmentplatform.enums.Levels;
import com.nu.assessmentplatform.helper.AssessmentHelper;
import com.nu.assessmentplatform.helper.UserHelper;
import com.nu.assessmentplatform.repo.AssessmentDetailsRepo;
import com.nu.assessmentplatform.repo.AssessmentQuestionsRepo;
import com.nu.assessmentplatform.repo.DomainsRepo;
import com.nu.assessmentplatform.repo.TestStatisticsRepo;
import com.nu.assessmentplatform.service.AssessmentService;
import com.nu.assessmentplatform.utils.EmailUtils;

import jakarta.mail.MessagingException;

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
	private AssessmentHelper assessmentHelper;

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
			if (domainsRepo.findByName(domains.getName()) != null) {
				responseDTO.setStatus("Domains already exists");
				responseDTO.setSuccess(Boolean.TRUE);
				return responseDTO;
			}
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
		assessmentQuestions.setTotalQuestionScore(questionSize * 10);
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
		if (existingTestStatics == null) {
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
				validateExistingAssessments(questionCode, userByEmail, false);
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

	@Override
	public ResponseDTO<ScoreResponse> getUserScore(String userEmail, String questionCode) {
		ResponseDTO<ScoreResponse> responseDTO = new ResponseDTO<>();
		ScoreResponse response = new ScoreResponse();
		try {
			Users userByEmail = userHelper.getUserByEmail(userEmail);
			if (userByEmail != null) {
				AssessmentDetails assessmentDetails = assessmentDetailsRepo
						.findByUserIdAndQuestionCode(userByEmail.getId(), questionCode);
				if (assessmentDetails != null) {
					response.setUserScore(assessmentDetails.getScore());
					response.setTotalQuestionScore(assessmentDetails.getTotalQuestionScore());
				}
				responseDTO.setState(response);
				responseDTO.setSuccess(Boolean.TRUE);
			}
			if (userByEmail == null) {
				throw new IllegalAccessException("User not found");
			}
		} catch (Exception e) {
			responseDTO.setSuccess(Boolean.FALSE);
			responseDTO.setErrors("Issue occured - Cause - " + e.getMessage());
		}
		return responseDTO;

	}

	@Override
	public ResponseDTO<List<AssessmentDetails>> fetchUsersAssignedAssessment(String userEmail,
			AssessmentStatus assessmentStatus) {
		ResponseDTO<List<AssessmentDetails>> responseDTO = new ResponseDTO<>();
		try {
			Users userByEmail = userHelper.getUserByEmail(userEmail);
			if (userByEmail != null) {
				List<AssessmentDetails> assessment = null;
				if (assessmentStatus != null) {
					assessment = assessmentDetailsRepo.findByUserIdAndAssessmentStatus(userByEmail.getId(),
							assessmentStatus);
				} else {
					assessment = assessmentDetailsRepo.findByUserId(userByEmail.getId());
				}
				if (assessment != null) {
					responseDTO.setState(assessment);
					responseDTO.setSuccess(Boolean.TRUE);
				}
			}
			if (userByEmail == null) {
				throw new IllegalAccessException("User not found");
			}
		} catch (Exception e) {
			responseDTO.setSuccess(Boolean.FALSE);
			responseDTO.setErrors("Issue occured - Cause - " + e.getMessage());
		}
		return responseDTO;
	}

	@Override
	public ResponseDTO<ScoreResponse> submitResponse(SubmitAssessmentRequest submitAssessmentRequest) {
		ResponseDTO<ScoreResponse> responseDTO = new ResponseDTO<>();
		ScoreResponse scoreResponse = new ScoreResponse();
		try {
			Users user = userHelper.fetchSingleUser(submitAssessmentRequest.getUserId());
			validateExistingAssessments(submitAssessmentRequest.getQuestionCode(), user, true);
			AssessmentQuestions assessmentQuestions = assessmentQuestionsRepo
					.findByQuestionCode(submitAssessmentRequest.getQuestionCode());
			if (assessmentQuestions != null) {
				List<QuestionsDetails> questionList = assessmentQuestions.getQuestionList();
				List<QuestionsDetails> submittedAnswers = submitAssessmentRequest.getQuestionsDetails();
				int score = calculateScore(questionList, submittedAnswers);
				updateScoreAndTestCountInDB(assessmentQuestions, submitAssessmentRequest.getUserId(), score);
				assessmentHelper.updateTestCount(assessmentQuestions.getDomainName(),
						assessmentQuestions.getDifficultyLevel(), assessmentQuestions.getQuestionCode(),
						user.getEmail());
				scoreResponse.setUserScore(score);
				scoreResponse.setTotalQuestionScore(assessmentQuestions.getTotalQuestionScore());
				responseDTO.setState(scoreResponse);
				responseDTO.setSuccess(Boolean.TRUE);
			}

		} catch (Exception e) {
			responseDTO.setSuccess(Boolean.FALSE);
			responseDTO.setErrors("Issue occured - Cause - " + e.getMessage());
		}
		return responseDTO;
	}

	private void updateScoreAndTestCountInDB(AssessmentQuestions assessmentQuestions, String userId, int score) {
		int count = 0;
		AssessmentDetails assessmentDetails = new AssessmentDetails();
		assessmentDetails.setAssessmentStatus(AssessmentStatus.COMPLETED);
		assessmentDetails.setDomain(assessmentQuestions.getDomainName());
		assessmentDetails.setLevel(assessmentQuestions.getDifficultyLevel());
		assessmentDetails.setQuestionCode(assessmentQuestions.getQuestionCode());
		assessmentDetails.setQuestionCount(assessmentQuestions.getQuestionCount());
		assessmentDetails.setScore(score);
		assessmentDetails.setUserId(userId);
		assessmentDetails.setTotalQuestionScore(100);
		assessmentDetails.setUserTestCount(++count);
		assessmentDetailsRepo.save(assessmentDetails);
	}

	public int calculateScore(List<QuestionsDetails> questionList, List<QuestionsDetails> submittedAnswers) {
		int totalQuestions = questionList.size();
		int correctResponses = 0;
		for (int i = 0; i < totalQuestions; i++) {
			QuestionsDetails question = questionList.get(i);
			QuestionsDetails submittedAnswer = submittedAnswers.get(i);

			if (!areQuestionsIdentical(question, submittedAnswer)) {
				throw new IllegalArgumentException("Questions are not identical.");
			}
			if (question.getCorrectOptionIndex() == submittedAnswer.getCorrectOptionIndex()) {
				correctResponses++;
			}
		}
		return (int) (correctResponses * 10);
	}

	private boolean areQuestionsIdentical(QuestionsDetails question1, QuestionsDetails submittedAnswers) {
		return question1.getQuestionText().contentEquals(submittedAnswers.getQuestionText());
	}

	private void generateAssessmentNotification(String userEmail) throws MessagingException {
		Context context = new Context();
		String html = templateEngine.process("assessment-notification-template", context);
		emailUtils.sendEmail(userEmail, "New Assessment Assigned for You", html);
	}

	private void saveAssessmentDetailsToDB(String questionCode, Users userByEmail, AssessmentQuestions questions) {
		AssessmentDetails assessmentDetails = new AssessmentDetails();
		assessmentDetails.setUserId(userByEmail.getId());
		assessmentDetails.setQuestionCode(questionCode);
		assessmentDetails.setDomain(questions.getDomainName());
		assessmentDetails.setLevel(questions.getDifficultyLevel());
		assessmentDetails.setQuestionCount(questions.getQuestionCount());
		assessmentDetails.setTotalQuestionScore(100);
		assessmentDetails.setAssessmentStatus(AssessmentStatus.ASSIGNED);
		assessmentDetailsRepo.save(assessmentDetails);
	}

	private void validateExistingAssessments(String questionCode, Users user, boolean submitApi) throws Exception {
		AssessmentDetails existingAssessment = assessmentDetailsRepo.findByUserIdAndQuestionCode(user.getId(),
				questionCode);
		if (existingAssessment != null && existingAssessment.getUserTestCount() >= 1) {
			throw new Exception("The assessment was already assigned and user have already took the test");
		}
		if (existingAssessment != null && !submitApi) {
			throw new Exception("The assessment was already assigned");
		}
	}
}
