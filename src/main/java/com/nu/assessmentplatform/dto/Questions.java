package com.nu.assessmentplatform.dto;

import java.util.List;

import lombok.Data;

@Data
public class Questions {
	private List<QuestionsDetails> questionList;
	private int questionCount;
}
