package org.example.courzelo.dto.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GradeRequest {
    private String name;
    private String moduleID;
    private String groupID;
    private String studentEmail;
    private double grade;
}
