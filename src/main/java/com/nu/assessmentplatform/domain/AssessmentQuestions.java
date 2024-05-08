package com.nu.assessmentplatform.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.nu.assessmentplatform.dto.QuestionsDetails;
import com.nu.assessmentplatform.enums.Levels;

import lombok.Data;

@Document("AssessmentQuestions")
@Data
public class AssessmentQuestions {

	@Id
	private String id;

	@Indexed(unique = true)
	@Field("questionCode")
	private String questionCode;

	@Field("questions")
	private List<QuestionsDetails> questionList;

	@Field("questionCount")
	private int questionCount;

	@Field("domainName")
	private String domainName;

	@Field("level")
	private Levels difficultyLevel;

	@Field("totalQuestionScore")
	private int totalQuestionScore;

}
