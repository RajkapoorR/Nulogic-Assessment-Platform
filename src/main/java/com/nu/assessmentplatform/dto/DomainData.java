package com.nu.assessmentplatform.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nu.assessmentplatform.enums.Levels;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class DomainData {
	private List<String> domains;
	private List<Levels> difficultyLevels;
}
