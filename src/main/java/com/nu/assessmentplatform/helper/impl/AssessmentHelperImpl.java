package com.nu.assessmentplatform.helper.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nu.assessmentplatform.domain.AssessmentDetails;
import com.nu.assessmentplatform.domain.TestStatistics;
import com.nu.assessmentplatform.domain.Users;
import com.nu.assessmentplatform.enums.Levels;
import com.nu.assessmentplatform.helper.AssessmentHelper;
import com.nu.assessmentplatform.repo.AssessmentDetailsRepo;
import com.nu.assessmentplatform.repo.TestStatisticsRepo;

@Component
public class AssessmentHelperImpl implements AssessmentHelper {

	@Autowired
	private TestStatisticsRepo testStatisticsRepo;

	@Autowired
	private AssessmentDetailsRepo assessmentDetailsRepo;

	@Override
	public void updateTestCount(String domain, Levels level, String questionCode, String email) {
		TestStatistics existingTestStatics = null;
		if (questionCode != null) {
			existingTestStatics = testStatisticsRepo.findByQuestionCode(questionCode);
		} else {
			existingTestStatics = testStatisticsRepo.findByDomainNameAndLevel(domain, level);
		}
		if (existingTestStatics != null) {
			int overallCount = existingTestStatics.getOverallCount();
			Set<String> userAttendees = existingTestStatics.getUserAttendees();
			existingTestStatics.setOverallCount(++overallCount);
			userAttendees.add(email);
			existingTestStatics.setUserAttendees(userAttendees);
			testStatisticsRepo.save(existingTestStatics);
		} else {
			Set<String> userAttendees = new HashSet<>();
			userAttendees.add(email);
			TestStatistics statistics = new TestStatistics();
			statistics.setDomainName(domain);
			statistics.setLevel(level);
			statistics.setOverallCount(1);
			statistics.setUserAttendees(userAttendees);
			statistics.setQuestionCode(questionCode);
			testStatisticsRepo.save(statistics);
		}
	}

	@Override
	public int fetchUserTestCount(String domain, Levels level, Users users, String questionCode) {
		AssessmentDetails assessmentDetails = null;
		if (domain != null && level != null && users != null) {
			assessmentDetails = assessmentDetailsRepo.findByUserIdAndDomainAndLevel(users.getId(), domain, level);
		}
		if (questionCode != null && users != null) {
			assessmentDetails = assessmentDetailsRepo.findByUserIdAndQuestionCode(users.getId(), questionCode);
		}

		if (assessmentDetails != null) {
			return assessmentDetails.getUserTestCount();
		} else {
			return 0;
		}
	}

}
