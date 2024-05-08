package com.nu.assessmentplatform.domain;

import lombok.Data;

@Data
public class SubmissionRequest {
    private String assessmentId;
    private int selectedOption;

}

