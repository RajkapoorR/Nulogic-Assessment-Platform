package com.nu.assessmentplatform.helper.impl;

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
	public void updateTestCount(String domain, Levels level) {
		TestStatistics existingTestStatics = testStatisticsRepo.findByDomainNameAndLevel(domain, level);
		if (existingTestStatics != null) {
			int overallCount = existingTestStatics.getOverallCount();
			existingTestStatics.setOverallCount(++overallCount);
			testStatisticsRepo.save(existingTestStatics);
		} else {
			TestStatistics statistics = new TestStatistics();
			statistics.setDomainName(domain);
			statistics.setLevel(level);
			statistics.setOverallCount(1);
			testStatisticsRepo.save(statistics);
		}
	}

	@Override
	public int fetchUserTestCount(String domain, Levels level, Users users) {
		AssessmentDetails assessmentDetails = assessmentDetailsRepo.findByUserAndDomainAndLevel(users, domain, level);
		if (assessmentDetails != null) {
			return assessmentDetails.getUserTestCount();
		} else {
			return 0;
		}
	}
}
