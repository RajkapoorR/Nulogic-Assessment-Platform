package com.nu.assessmentplatform.helper;

import org.springframework.stereotype.Component;

import com.nu.assessmentplatform.domain.Users;
import com.nu.assessmentplatform.enums.Levels;

@Component
public interface AssessmentHelper {

	int fetchUserTestCount(String domain, Levels level, Users users, String questionCode);

	void updateTestCount(String domain, Levels level, String questionCode, String email);

}
