package org.example.courzelo.dto.responses.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModuleResponse {
    private String id;
    private String name;
    private String description;
    private String duration;
    private int credit;
    private String institutionID;
    private String program;
}
