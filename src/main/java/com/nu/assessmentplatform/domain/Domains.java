package com.nu.assessmentplatform.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nu.assessmentplatform.enums.Levels;

import lombok.Data;

@Document(collection = "Domains")
@Data
public class Domains {
    @Id
    private String id;
    private String name;
    private List<Levels> levels = new ArrayList<>();

}
