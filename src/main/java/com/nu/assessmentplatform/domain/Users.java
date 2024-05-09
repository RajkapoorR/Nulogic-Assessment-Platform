package com.nu.assessmentplatform.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.ToString;

@Document(collection = "Users")
@Data
@ToString
public class Users {
	@Id
	private String id;

	@Field("userRole")
	private String userRole;

	@Field("isActive")
	private boolean isActive;

	@Field("googleAuthId")
	private String googleAuthId;

	@Field("firstName")
	private String firstName;

	@Field("lastName")
	private String lastName;

	@Field("displayName")
	private String displayName;

	@Field("email")
	private String email;

	@Field("password")
	private String password;

	@Field("createdAt")
	private Date createdAt;

	@Field("updatedAt")
	private Date updatedAt;

}
