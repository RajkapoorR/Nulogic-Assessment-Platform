package com.nu.assessmentplatform.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nu.assessmentplatform.domain.Users;

@Repository
public interface UsersRepo extends MongoRepository<Users, String> {

	Users findByEmail(String email);

}
