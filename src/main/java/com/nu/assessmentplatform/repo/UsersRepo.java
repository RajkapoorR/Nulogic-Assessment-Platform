package com.nu.assessmentplatform.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.nu.assessmentplatform.domain.Users;

@Repository
public interface UsersRepo extends MongoRepository<Users, String> {

	Users findByEmail(String email);
	
    @Query("{ 'email': { '$regex': '^?0', '$options': 'i' } }")
    List<Users> findEmailsByPrefix(String prefix);

}
