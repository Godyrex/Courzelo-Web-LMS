package org.example.courzelo.models.institution;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document(collection = "modules")
public class Module {
    @Id
    private String id;
    private String name;
    private String description;
    private String duration;
    private List<String> skills;
    private Semester semester;
    private int credit;
    private List<Assessment> assessments;
    private String institutionID;
    private String program;
}

