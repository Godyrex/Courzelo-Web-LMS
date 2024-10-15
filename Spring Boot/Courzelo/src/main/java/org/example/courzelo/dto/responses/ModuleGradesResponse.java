package org.example.courzelo.dto.responses;

import lombok.Builder;
import lombok.Data;
import org.example.courzelo.dto.responses.module.ModuleResponse;

import java.util.List;

@Data
@Builder
public class ModuleGradesResponse {
    private ModuleResponse module;
    private List<GradeResponse> grades;
}
