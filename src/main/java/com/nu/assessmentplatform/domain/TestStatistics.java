package com.nu.assessmentplatform.domain;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nu.assessmentplatform.enums.Levels;

import lombok.Data;

@Data
@Document(collection = "TestStatistics")
public class TestStatistics {
	@Id
	private String id;
	private String domainName;
	private Levels level;
	private String questionCode;
	private int overallCount;
    private Set<String> userAttendees;
}
