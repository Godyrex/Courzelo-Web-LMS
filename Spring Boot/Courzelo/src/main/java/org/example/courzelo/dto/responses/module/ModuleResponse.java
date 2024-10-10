package org.example.courzelo.dto.responses.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.courzelo.models.institution.Assessment;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModuleResponse {
    private String id;
    private String name;
    private String semester;
    private String description;
    private List<String> skills;
    private String duration;
    private int credit;
    private List<Assessment> assessments;
    private String institutionID;
    private String program;
}
