package com.nu.assessmentplatform.dto;

import lombok.Data;

@Data
public class QuestionsDetails {
	private Integer questionNumber;
	private String questionText;
	private String[] options = new String[4];
	private int correctOptionIndex;
}
