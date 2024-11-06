package org.example.courzelo.services.Timetable;

import lombok.AllArgsConstructor;
import org.example.courzelo.dto.Timetable.ElementModuleDTO;
import org.example.courzelo.dto.responses.CourseResponse;
import org.example.courzelo.dto.responses.GroupResponse;
import org.example.courzelo.dto.responses.UserProfileResponse;
import org.example.courzelo.dto.responses.UserResponse;
import org.example.courzelo.models.User;
import org.example.courzelo.models.institution.Course;
import org.example.courzelo.models.institution.Group;
import org.example.courzelo.models.institution.Institution;
import org.example.courzelo.serviceImpls.CourseServiceImpl;
import org.example.courzelo.serviceImpls.GroupServiceImpl;
import org.example.courzelo.serviceImpls.InstitutionServiceImpl;
import org.example.courzelo.serviceImpls.UserServiceImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DataFromDB {
    private final ElementModuleService elementModuleService;
    private final CourseServiceImpl courseService;
    private final GroupServiceImpl groupService;
    private final UserServiceImpl professorService;
    private final InstitutionServiceImpl institutionService;
    public static List<ElementModuleDTO> elementModules;
    public static List<User> professors;
    public static List<CourseResponse> courses;
    public static List<GroupResponse> groups;
    public void loadDataFromDatabase() {
        //elementModules = elementModuleService.getAllElementModules();
        professors = elementModuleService.getProfsByRole();
        courses = courseService.findAll();
        groups = elementModuleService.getClasses();
    }
}
