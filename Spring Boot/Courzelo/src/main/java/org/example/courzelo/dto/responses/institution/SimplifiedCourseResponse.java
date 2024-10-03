package org.example.courzelo.dto.responses.institution;

import lombok.Builder;
import lombok.Data;
import org.example.courzelo.models.Timetable.ElementModule;

import java.util.List;

@Data
@Builder
public class SimplifiedCourseResponse {
    private String courseID;
    private String courseName;
    private String module;
    private List<ElementModule> elementModules;


}
