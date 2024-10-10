package org.example.courzelo.services;

import org.example.courzelo.dto.requests.GradeRequest;
import org.example.courzelo.dto.responses.GradeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IGradeService {
    ResponseEntity<HttpStatus> createGrade(GradeRequest gradeRequest);
    ResponseEntity<HttpStatus> updateGrade(String gradeID, GradeRequest gradeRequest);
    ResponseEntity<HttpStatus> deleteGrade(String gradeID);
    ResponseEntity<List<GradeResponse>> getGradesByGroup(String groupID);

    ResponseEntity<List<GradeResponse>> getGradesByGroupAndModule(String groupID, String moduleID);

    ResponseEntity<HttpStatus> createGrades(List<GradeRequest> gradeRequests);
}
