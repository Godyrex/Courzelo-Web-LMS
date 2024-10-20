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
    private Double scoreToPass;
    private List<String> skills;
    private String duration;
    private Boolean isFinished;
    private int credit;
    private List<Assessment> assessments;
    private String institutionID;
    private String program;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleResponse that = (ModuleResponse) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
