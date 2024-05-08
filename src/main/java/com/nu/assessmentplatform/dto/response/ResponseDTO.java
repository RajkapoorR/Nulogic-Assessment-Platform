package com.nu.assessmentplatform.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class ResponseDTO<T> {
	private T state;

	private boolean success;
	private String message;
	private Object errors;
	
	private String status;

	public void setMessage(String answerSubmittedSuccessfully) {
	}
	public static <T> ResponseDTO<T> error(String message) {

		return null;
	}
		public void setData(T updatedAssessment) {
	}
}
