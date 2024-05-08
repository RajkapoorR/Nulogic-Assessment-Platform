package com.nu.assessmentplatform.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nu.assessmentplatform.dto.QuestionsDetails;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class SubmitAssessmentRequest {
	private List<QuestionsDetails> questionsDetails;
	private String questionCode;
	private String userId;
}
