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

	public void incrementUserTestCount() {
		this.userTestCount++;
	}

	// Method to update question count and correct answers count after user submission
	public void updateUserSubmission(boolean correctAnswer) {
		this.questionCount++; // Increment question count for every submission
		if (correctAnswer) {
			this.score++; // Increment score if the answer is correct
		}
	}

	// Method to check if user can attend the test based on userTestCount
	public boolean canUserAttendTest() {
		return this.userTestCount <= 1; // Return true if userTestCount is less than or equal to 1
	}

	public void setSelectedOption(int selectedOption) {
	}
}
