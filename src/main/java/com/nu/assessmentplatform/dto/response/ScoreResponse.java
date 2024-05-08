package com.nu.assessmentplatform.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class ScoreResponse {
	private int userScore;
	private int totalQuestionScore;
}
