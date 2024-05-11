package com.nu.assessmentplatform.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nu.assessmentplatform.domain.AssessmentQuestions;
import com.nu.assessmentplatform.enums.Levels;

@Repository
public interface AssessmentQuestionsRepo extends MongoRepository<AssessmentQuestions, String> {

	AssessmentQuestions findByDomainNameAndDifficultyLevel(String domainName, Levels levels);

	AssessmentQuestions findByQuestionCode(String questionCode);

	AssessmentQuestions findByDomainNameAndDifficultyLevelAndQuestionCode(String domainName, Levels levels,
			String questionCode);

}
