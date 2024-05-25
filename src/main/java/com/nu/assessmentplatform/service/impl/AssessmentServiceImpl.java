package com.nu.assessmentplatform.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.fasterxml.jackson.databind.ObjectMapper;
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
	public ResponseDTO<DomainData> fetchAllDomains(String email) {
		ResponseDTO<DomainData> responseDTO = new ResponseDTO<>();
		Users userByEmail = userHelper.getUserByEmail(email);
		if (userByEmail != null) {
			List<String> userWorkingDomains = userByEmail.getWorkingDomains();
			List<Domains> allDomainData = domainsRepo.findAll();
			List<String> matchedDomains = new ArrayList<>();
			allDomainData.stream().forEach(domain -> {
				if (userWorkingDomains.contains(domain.getName())) {
					matchedDomains.add(domain.getName());
				}
			});
			DomainData data = new DomainData();
			data.setDomains(matchedDomains);
			responseDTO.setState(data);
			responseDTO.setSuccess(Boolean.TRUE);
		} else {
			responseDTO.setSuccess(Boolean.FALSE);
			responseDTO.setErrors("Users not found");
		}
		return responseDTO;
	}

	@Override
	public ResponseDTO<DomainData> fetchAllQuestionCode(String email) {
		ResponseDTO<DomainData> responseDTO = new ResponseDTO<>();
		Users userByEmail = userHelper.getUserByEmail(email);
		if (userByEmail != null) {
			List<String> userWorkingDomains = userByEmail.getWorkingDomains();
			List<String> questionCodeList = new ArrayList<>();
			List<AssessmentQuestions> assessmentQuestions = assessmentQuestionsRepo.findAll();
			assessmentQuestions.stream().filter(question -> userWorkingDomains.contains(question.getDomainName()))
					.forEach(question -> questionCodeList.add(question.getQuestionCode()));
			DomainData data = new DomainData();
			data.setDomains(questionCodeList);
			responseDTO.setState(data);
			responseDTO.setSuccess(Boolean.TRUE);
		} else {
			responseDTO.setSuccess(Boolean.FALSE);
			responseDTO.setErrors("User not found");
		}
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
	public ResponseDTO<Questions> fetchQuestions(String domainName, Levels difficultyLevel, String questionCode,
			String userEmail) {
		ResponseDTO<Questions> responseDTO = new ResponseDTO<>();
		Questions questions = new Questions();
		AssessmentQuestions assessmentQuestions;
		Users userByEmail = userHelper.getUserByEmail(userEmail);
		try {
			if (!StringUtils.hasText(userEmail) || userEmail.equalsIgnoreCase("undefined")) {
				throw new IllegalAccessException("Email is Null/Empty");
			}
			validateExistingAssessments(questionCode, userByEmail, true);
			if (StringUtils.hasText(questionCode) && StringUtils.hasText(difficultyLevel.name())
					&& StringUtils.hasText(domainName)) {
				assessmentQuestions = assessmentQuestionsRepo
						.findByDomainNameAndDifficultyLevelAndQuestionCode(domainName, difficultyLevel, questionCode);
				if (assessmentQuestions != null) {
					questions.setQuestionList(assessmentQuestions.getQuestionList());
					questions.setQuestionCount(assessmentQuestions.getQuestionList().size());
					questions.setQuestionCode(questionCode);
					questions.setDomain(domainName);
					questions.setLevel(difficultyLevel);
				}

			} else if (StringUtils.hasText(questionCode)) {
				assessmentQuestions = assessmentQuestionsRepo.findByQuestionCode(questionCode);
				if (assessmentQuestions != null) {
					questions.setQuestionList(assessmentQuestions.getQuestionList());
					questions.setQuestionCount(assessmentQuestions.getQuestionList().size());
					questions.setQuestionCode(questionCode);
					questions.setDomain(domainName);
					questions.setLevel(difficultyLevel);
				}

			} else {
				assessmentQuestions = assessmentQuestionsRepo.findByDomainNameAndDifficultyLevel(domainName,
						difficultyLevel);
				if (assessmentQuestions != null) {
					questions.setQuestionList(assessmentQuestions.getQuestionList());
					questions.setQuestionCount(assessmentQuestions.getQuestionList().size());
					questions.setQuestionCode(assessmentQuestions.getQuestionCode());
					questions.setDomain(domainName);
					questions.setLevel(difficultyLevel);
				}
			}
			if (assessmentQuestions == null) {
				throw new IllegalArgumentException("Questions not found");
			}
			responseDTO.setState(questions);
			responseDTO.setSuccess(Boolean.TRUE);
		} catch (Exception e) {
			responseDTO.setErrors("Issue occured - Cause - " + e.getMessage());
			responseDTO.setSuccess(Boolean.FALSE);
		}
		return responseDTO;
	}

	@Override
	public ResponseDTO<List<TestStatistics>> getStaticsData(String domain, Levels level) {
		ResponseDTO<List<TestStatistics>> responseDTO = new ResponseDTO<>();
		if (domain != null && level != null) {
			TestStatistics existingTestStatics = testStatisticsRepo.findByDomainNameAndLevel(domain, level);
			if (existingTestStatics != null) {
				responseDTO.setSuccess(Boolean.TRUE);
				responseDTO.setState(Arrays.asList(existingTestStatics));
			}
			if (existingTestStatics == null) {
				responseDTO.setSuccess(Boolean.TRUE);
				responseDTO.setState(Collections.emptyList());
			}
		} else {
			List<TestStatistics> testStatistics = testStatisticsRepo.findAll();
			if (testStatistics != null) {
				responseDTO.setSuccess(Boolean.TRUE);
				responseDTO.setState(testStatistics);
			}
			if (testStatistics == null) {
				responseDTO.setSuccess(Boolean.TRUE);
				responseDTO.setState(Collections.emptyList());
			}
		}
		return responseDTO;
	}

	@Override
	public ResponseDTO<?> assignTask(List<String> userEmailList, String questionCode) throws MessagingException {
		ResponseDTO<?> responseDTO = new ResponseDTO<>();
		AtomicInteger successCount = new AtomicInteger();
		AtomicReference<String> errors = new AtomicReference<>();
		userEmailList.stream().forEach(userEmail -> {
			try {
				Users userByEmail = userHelper.getUserByEmail(userEmail);
				if (!StringUtils.hasText(userEmail) || userEmail.equalsIgnoreCase("undefined")) {
					throw new IllegalAccessException("Email is Null/Empty");
				}
				AssessmentQuestions questions = assessmentQuestionsRepo.findByQuestionCode(questionCode);
				if (userByEmail != null && questions != null) {
					validateExistingAssessments(questionCode, userByEmail, false);
					generateAssessmentNotification(userEmail, userByEmail.getDisplayName());
					saveAssessmentDetailsToDB(questionCode, userByEmail, questions);
				}
				if (questions == null) {
					throw new IllegalAccessException("Invalid question code");
				}
				if (userByEmail == null) {
					throw new IllegalAccessException("User not found");
				}
				successCount.getAndIncrement();
			} catch (Exception e) {
				errors.set("Issue while assigning the assessment - Cause - " + e.getMessage());
			}
		});
		if (successCount.get() == userEmailList.size()) {
			responseDTO.setSuccess(Boolean.TRUE);
			responseDTO.setStatus(
					"Assessment assigned successfully and notified to the employees via mail -" + userEmailList);
		} else {
			responseDTO.setSuccess(Boolean.FALSE);
			responseDTO.setErrors(errors);
		}
		return responseDTO;
	}

	@Override
	public ResponseDTO<List<AssessmentDetails>> getUserScore(String userEmail) {
		ResponseDTO<List<AssessmentDetails>> responseDTO = new ResponseDTO<>();
		try {
			Users userByEmail = userHelper.getUserByEmail(userEmail);
			if (userByEmail != null) {
				List<AssessmentDetails> assessmentDetails = assessmentDetailsRepo
						.findByUserIdAndAssessmentStatus(userByEmail.getId(), AssessmentStatus.COMPLETED);
				responseDTO.setState(assessmentDetails);
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
			List<AssessmentStatus> assessmentStatus) {
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
			} else if (assessmentQuestions == null) {
				throw new IllegalArgumentException("Questions not found");
			}

		} catch (Exception e) {
			responseDTO.setSuccess(Boolean.FALSE);
			responseDTO.setErrors("Issue occured - Cause - " + e.getMessage());
		}
		return responseDTO;
	}

	@Override
	public ResponseDTO<?> readAndSaveAssessmentQuestion(MultipartFile file) {
		ResponseDTO<?> responseDTO = new ResponseDTO<>();
		String line;
		ObjectMapper mapper = new ObjectMapper();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			while ((line = br.readLine()) != null) {
				AssessmentQuestions assessmentQuestions = mapper.readValue(line, AssessmentQuestions.class);
				int questionSize = !CollectionUtils.isEmpty(assessmentQuestions.getQuestionList())
						? assessmentQuestions.getQuestionList().size()
						: 0;
				assessmentQuestions.setQuestionCount(questionSize);
				assessmentQuestions.setTotalQuestionScore(questionSize * 10);
				assessmentQuestionsRepo.save(assessmentQuestions);
				responseDTO.setSuccess(Boolean.TRUE);
				responseDTO.setStatus("Questions uploaded successfully");
			}
		} catch (Exception e) {
			responseDTO.setSuccess(Boolean.FALSE);
			responseDTO.setErrors("Issue occured - Cause - " + e.getMessage());
		}
		return responseDTO;
	}

	@Override
	public ResponseDTO<?> importQuestionsFromCSV(MultipartFile file) {
		ResponseDTO<?> responseDTO = new ResponseDTO<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			Iterable<CSVRecord> records = CSVFormat.DEFAULT
					.withHeader("questionCode", "domainName", "difficultyLevel", "questionNumber", "questionText",
							"option1", "option2", "option3", "option4", "correctOption")
					.withSkipHeaderRecord().parse(reader);
			List<QuestionsDetails> questionList = new ArrayList<>();
			String questionCode = null;
			String domainName = null;
			Levels difficultyLevel = null;
			for (CSVRecord record : records) {
				if (questionCode == null) {
					questionCode = record.get("questionCode");
					domainName = record.get("domainName");
					difficultyLevel = Levels.valueOf(record.get("difficultyLevel").toUpperCase());
				}
				QuestionsDetails questionDetails = new QuestionsDetails();
				questionDetails.setQuestionNumber(Integer.parseInt(record.get("questionNumber")));
				questionDetails.setQuestionText(record.get("questionText"));
				questionDetails.setOptions(new String[] { record.get("option1"), record.get("option2"),
						record.get("option3"), record.get("option4") });
				questionDetails.setCorrectOption(record.get("correctOption"));
				questionList.add(questionDetails);
			}
			AssessmentQuestions assessmentQuestions = new AssessmentQuestions();
			assessmentQuestions.setQuestionCode(questionCode);
			assessmentQuestions.setDomainName(domainName);
			assessmentQuestions.setDifficultyLevel(difficultyLevel);
			assessmentQuestions.setQuestionList(questionList);
			assessmentQuestions.setQuestionCount(questionList.size());
			assessmentQuestions.setTotalQuestionScore(100);
			validateAssessmentQuestions(questionCode);
			assessmentQuestionsRepo.save(assessmentQuestions);
			responseDTO.setSuccess(Boolean.TRUE);
			responseDTO.setStatus("Uploaded the file successfully: " + file.getOriginalFilename());
		} catch (Exception e) {
			responseDTO.setSuccess(Boolean.FALSE);
			responseDTO.setErrors("Issue occured - Cause - " + e.getMessage());
		}
		return responseDTO;
	}

	private void validateAssessmentQuestions(String questionCode) {
		List<AssessmentQuestions> assessmentQuestionsList = assessmentQuestionsRepo.findAll();
		boolean questionExists = assessmentQuestionsList.stream()
				.anyMatch(question -> question.getQuestionCode().equals(questionCode));
		if (questionExists) {
			throw new RuntimeException("Question set for the question code already exists");
		}
	}

	private void updateScoreAndTestCountInDB(AssessmentQuestions assessmentQuestions, String userId, int score) {
		int count = 0;
		AssessmentDetails existingAssessment = assessmentDetailsRepo.findByUserIdAndQuestionCode(userId,
				assessmentQuestions.getQuestionCode());
		if (existingAssessment != null) {
			existingAssessment.setAssessmentStatus(AssessmentStatus.COMPLETED);
			existingAssessment.setScore(score);
			existingAssessment.setUserTestCount(++count);
			if (existingAssessment.getUserTestCount() >= 2) {
				existingAssessment.setAssessmentBlocked(Boolean.TRUE);
			}
			assessmentDetailsRepo.save(existingAssessment);
		} else {
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
	}

	@Override
	public ResponseDTO<?> updateAssessmentDetails(String userEmail, String questionCode) throws MessagingException {
		ResponseDTO<?> responseDTO = new ResponseDTO<>();
		Users user = userHelper.fetchUserByEmail(userEmail);
		AssessmentDetails assessmentDetails = assessmentDetailsRepo.findByUserIdAndQuestionCode(user.getId(),
				questionCode);
		if (assessmentDetails != null) {
			if (!assessmentDetails.isAssessmentBlocked()) {
				responseDTO.setSuccess(Boolean.FALSE);
				responseDTO.setErrors("Assessment is already unlocked");
			} else {
				assessmentDetails.setAssessmentBlocked(Boolean.FALSE);
				assessmentDetails.setUserTestCount(0);
				assessmentDetails.setAssessmentStatus(AssessmentStatus.REASSIGNED);
				assessmentDetailsRepo.save(assessmentDetails);
				generateAssessmentUnblockedNotification(userEmail, user.getDisplayName());
				responseDTO.setSuccess(Boolean.TRUE);
				responseDTO.setStatus("Unblocked the assessment for the user..Now they can take the test again..");

			}
		} else {
			responseDTO.setSuccess(Boolean.FALSE);
			responseDTO.setErrors("No assessment was there under the user ");
		}
		return responseDTO;
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
			if (question.getCorrectOption().equalsIgnoreCase(submittedAnswer.getCorrectOption())) {
				correctResponses++;
			}
		}
		return (int) (correctResponses * 10);
	}

	private boolean areQuestionsIdentical(QuestionsDetails question1, QuestionsDetails submittedAnswers) {
		return question1.getQuestionText().contentEquals(submittedAnswers.getQuestionText());
	}

	private void generateAssessmentNotification(String userEmail, String name) throws MessagingException {
		ModelMap map = new ModelMap();
		Context context = new Context();
		map.addAttribute("employeeName", name);
		context.setVariables(map);
		String html = templateEngine.process("assessment-notification-template", context);
		emailUtils.sendEmail(userEmail, "New Assessment Assigned for You", html);
	}

	private void generateAssessmentUnblockedNotification(String userEmail, String name) throws MessagingException {
		ModelMap map = new ModelMap();
		Context context = new Context();
		map.addAttribute("employeeName", name);
		context.setVariables(map);
		String html = templateEngine.process("assessment-unlocked-template", context);
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
		if (existingAssessment != null && existingAssessment.getUserTestCount() >= 2) {
			throw new Exception(
					"The assessment was already assigned and user have already took the test twice..Please contact your admin to reattempt..");
		}
		if (existingAssessment != null && !submitApi) {
			throw new Exception("The assessment was already assigned");
		}
	}
}
