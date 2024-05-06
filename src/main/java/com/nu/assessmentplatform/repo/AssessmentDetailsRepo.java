package com.nu.assessmentplatform.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nu.assessmentplatform.domain.AssessmentDetails;
import com.nu.assessmentplatform.domain.Users;
import com.nu.assessmentplatform.enums.Levels;

@Repository
public interface AssessmentDetailsRepo extends MongoRepository<AssessmentDetails, String> {
	AssessmentDetails findByUserAndDomainAndLevel(Users user, String domain, Levels level);

	AssessmentDetails findByUserAndQuestionCode(Users user, String questionCode);

	AssessmentDetails findByQuestionCode(String questionCode);

}
