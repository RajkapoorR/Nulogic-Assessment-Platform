package com.nu.assessmentplatform.helper;

import org.springframework.stereotype.Component;

import com.nu.assessmentplatform.domain.Users;
import com.nu.assessmentplatform.enums.Levels;

@Component
public interface AssessmentHelper {

	void updateTestCount(String domain, Levels level);

	int fetchUserTestCount(String domain, Levels level, Users users);

}
