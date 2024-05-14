package com.nu.assessmentplatform.dto;

import java.util.List;

import com.nu.assessmentplatform.enums.Levels;

import lombok.Data;

@Data
public class Questions {
	private List<QuestionsDetails> questionList;
	private int questionCount;
	private String questionCode;
	private String domain;
	private Levels level;
}
