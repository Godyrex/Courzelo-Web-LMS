package org.example.courzelo.dto.responses;

import lombok.Builder;
import lombok.Data;
import org.example.courzelo.dto.responses.institution.SimplifiedCourseResponse;
import org.example.courzelo.models.Timetable.Semester;

import java.util.List;

@Data
@Builder
public class GroupResponse {
    private String id;
    private String name;
    private String institutionID;
    private List<String> students;
    private List<SimplifiedCourseResponse> courses;
    private List<Semester>semesters;
}
