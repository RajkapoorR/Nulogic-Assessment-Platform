package com.nu.assessmentplatform.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class UserDTO {

	private String id;
	
	private String userRole;

	private String firstName;

	private String lastName;

	private String displayName;

	private String email;
	
	private List<String> domains;

}
