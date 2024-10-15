package org.example.courzelo.serviceImpls;

import lombok.AllArgsConstructor;
import org.example.courzelo.exceptions.CourseNotFoundException;
import org.example.courzelo.models.institution.Course;
import org.example.courzelo.models.institution.Institution;
import org.example.courzelo.models.institution.Timeslot;
import org.example.courzelo.repositories.CourseRepository;
import org.example.courzelo.repositories.InstitutionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class TimetableGenerationService {
    private final CourseRepository courseRepository;

    private final InstitutionRepository institutionRepository;

    private final TimetableService timetableService;


    public ResponseEntity<HttpStatus> generateWeeklyTimetable(String institutionID) {
        List<Course> courses = courseRepository.findAllByInstitutionID(institutionID)
                .orElseThrow(()-> new CourseNotFoundException("No courses found for institution"));

        // Generate timetable using the Greedy algorithm
        timetableService.generateTimetable(courses,institutionID);

        // Save or display timetables
        Map<String, List<Timeslot>> groupTimetables = timetableService.getGroupTimetables();
        Map<String, List<Timeslot>> teacherTimetables = timetableService.getTeacherTimetables();
        Institution institution = institutionRepository.findById(institutionID)
                .orElseThrow(()-> new CourseNotFoundException("Institution not found"));
        institution.setGroupTimetables(groupTimetables);
        institution.setTeacherTimetables(teacherTimetables);
        institutionRepository.save(institution);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

