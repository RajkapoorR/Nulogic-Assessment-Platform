package com.nu.assessmentplatform.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nu.assessmentplatform.domain.TestStatistics;
import com.nu.assessmentplatform.enums.Levels;

@Repository
public interface TestStatisticsRepo extends MongoRepository<TestStatistics, String> {

	TestStatistics findByDomainNameAndLevel(String domainName, Levels level);

	TestStatistics findByQuestionCode(String questionCode);
}
