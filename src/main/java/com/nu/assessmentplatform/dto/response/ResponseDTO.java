package com.nu.assessmentplatform.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class ResponseDTO<T> {
	private T state;

	private boolean success;

	private Object errors;
	
	private String status;
}
