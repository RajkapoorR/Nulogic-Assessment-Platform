package com.nu.assessmentplatform.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nu.assessmentplatform.domain.AssessmentDetails;
import com.nu.assessmentplatform.enums.AssessmentStatus;
import com.nu.assessmentplatform.enums.Levels;

@Repository
public interface AssessmentDetailsRepo extends MongoRepository<AssessmentDetails, String> {
	AssessmentDetails findByUserIdAndDomainAndLevel(String userId, String domain, Levels level);

	AssessmentDetails findByUserIdAndQuestionCode(String userId, String questionCode);

	List<AssessmentDetails> findByUserIdAndAssessmentStatus(String userId, AssessmentStatus assessmentStatus);

	List<AssessmentDetails> findByUserId(String userId);

}
