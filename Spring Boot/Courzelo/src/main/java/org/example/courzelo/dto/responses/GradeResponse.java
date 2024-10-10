package org.example.courzelo.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GradeResponse {
    private String id;
    private String name;
    private String moduleID;
    private String institutionID;
    private String groupID;
    private String studentEmail;
    private double grade;
}
