package com.nu.assessmentplatform.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nu.assessmentplatform.domain.Domains;

@Repository
public interface DomainsRepo extends MongoRepository<Domains, String> {
	Domains findByName(String name);
}
