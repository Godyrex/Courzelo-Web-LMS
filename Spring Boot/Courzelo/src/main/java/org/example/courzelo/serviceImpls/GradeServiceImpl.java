package org.example.courzelo.serviceImpls;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.courzelo.dto.requests.GradeRequest;
import org.example.courzelo.dto.responses.GradeResponse;
import org.example.courzelo.exceptions.GradeNotFoundException;
import org.example.courzelo.exceptions.UserNotFoundException;
import org.example.courzelo.models.User;
import org.example.courzelo.models.institution.Grade;
import org.example.courzelo.repositories.GradeRepository;
import org.example.courzelo.repositories.UserRepository;
import org.example.courzelo.services.IGradeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class GradeServiceImpl implements IGradeService {
    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<HttpStatus> createGrade(GradeRequest gradeRequest) {
        Grade existingGrade = gradeRepository.findByNameAndModuleIDAndStudentEmail(
                        gradeRequest.getName(), gradeRequest.getModuleID(), gradeRequest.getStudentEmail())
                .orElse(null);

        if (existingGrade != null) {
            existingGrade.setGrade(gradeRequest.getGrade());
            gradeRepository.save(existingGrade);
        } else {
            User user = userRepository.findByEmail(gradeRequest.getStudentEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            Grade newGrade = Grade.builder()
                    .name(gradeRequest.getName())
                    .moduleID(gradeRequest.getModuleID())
                    .groupID(gradeRequest.getGroupID())
                    .institutionID(user.getEducation().getInstitutionID())
                    .studentEmail(gradeRequest.getStudentEmail())
                    .grade(gradeRequest.getGrade())
                    .build();

            gradeRepository.save(newGrade);

            if (user.getEducation().getGrades() == null) {
                user.getEducation().setGrades(new ArrayList<>());
            }
            user.getEducation().getGrades().add(newGrade.getId());
            userRepository.save(user);

            log.info("Grade created: {}", newGrade);
        }

        return ResponseEntity.ok(HttpStatus.CREATED);
    }
    @Override
    public ResponseEntity<HttpStatus> createGrades(List<GradeRequest> gradeRequests) {
        gradeRequests.forEach(this::createGrade);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<HttpStatus> updateGrade(String gradeID, GradeRequest gradeRequest) {
       Grade grade = gradeRepository.findById(gradeID).orElseThrow(() -> new GradeNotFoundException("Grade not found"));
         grade.setName(gradeRequest.getName());
            grade.setModuleID(gradeRequest.getModuleID());
            grade.setGroupID(gradeRequest.getGroupID());
            grade.setStudentEmail(gradeRequest.getStudentEmail());
            grade.setGrade(gradeRequest.getGrade());
            gradeRepository.save(grade);
            log.info("Grade updated: {}", grade);
            return ResponseEntity.ok(HttpStatus.OK);

    }

    @Override
    public ResponseEntity<HttpStatus> deleteGrade(String gradeID) {
        Grade grade = gradeRepository.findById(gradeID).orElseThrow(() -> new GradeNotFoundException("Grade not found"));
        gradeRepository.delete(grade);
        log.info("Grade deleted: {}", grade);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<GradeResponse>> getGradesByGroup(String groupID) {
        List<Grade> grades = gradeRepository.findAllByGroupID(groupID).orElseThrow(() -> new GradeNotFoundException("Grades not found"));
        List<GradeResponse> gradeResponses = new ArrayList<>();
        grades.forEach(grade -> gradeResponses.add(GradeResponse.builder()
                .id(grade.getId())
                .name(grade.getName())
                .moduleID(grade.getModuleID())
                .groupID(grade.getGroupID())
                .institutionID(grade.getInstitutionID())
                .studentEmail(grade.getStudentEmail())
                .grade(grade.getGrade())
                .build()));
        log.info("Grades found: {}", gradeResponses);
        return ResponseEntity.ok(gradeResponses);
    }

    @Override
    public ResponseEntity<List<GradeResponse>> getGradesByGroupAndModule(String groupID, String moduleID) {
        List<Grade> grades = gradeRepository.findAllByGroupIDAndModuleID(groupID, moduleID).orElseThrow(() -> new GradeNotFoundException("Grades not found"));
        List<GradeResponse> gradeResponses = new ArrayList<>();
        grades.forEach(grade -> gradeResponses.add(GradeResponse.builder()
                .id(grade.getId())
                .name(grade.getName())
                .moduleID(grade.getModuleID())
                .groupID(grade.getGroupID())
                .institutionID(grade.getInstitutionID())
                .studentEmail(grade.getStudentEmail())
                .grade(grade.getGrade())
                .build()));
        log.info("Grades found: {}", gradeResponses);
        return ResponseEntity.ok(gradeResponses);
    }


}
