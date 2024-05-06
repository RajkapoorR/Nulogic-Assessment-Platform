package com.nu.assessmentplatform.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nu.assessmentplatform.enums.Levels;

import lombok.Data;

@Document("AssessmentDetails")
@Data
public class AssessmentDetails {

	@Id
	private String id;

	private String domain;

	private Levels level;
	
	@Indexed(unique = true)
	private String questionCode;

	private Users user;

	private int score;
	
	private int questionCount;

	private int userTestCount;

}
