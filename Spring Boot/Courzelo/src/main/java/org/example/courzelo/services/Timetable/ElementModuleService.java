package org.example.courzelo.services.Timetable;

import lombok.AllArgsConstructor;
import org.example.courzelo.dto.Timetable.ElementModuleDTO;
import org.example.courzelo.dto.responses.CourseResponse;
import org.example.courzelo.dto.responses.GroupResponse;
import org.example.courzelo.dto.responses.UserResponse;
import org.example.courzelo.dto.responses.institution.SimplifiedCourseResponse;
import org.example.courzelo.models.Role;
import org.example.courzelo.models.Timetable.ElementModule;
import org.example.courzelo.models.Timetable.Period;
import org.example.courzelo.models.Timetable.Semester;
import org.example.courzelo.models.User;
import org.example.courzelo.models.institution.Course;
import org.example.courzelo.models.institution.Group;
import org.example.courzelo.repositories.CourseRepository;
import org.example.courzelo.repositories.GroupRepository;
import org.example.courzelo.repositories.Timetable.ElementModuleRepo;
import org.example.courzelo.repositories.UserRepository;
import org.example.courzelo.serviceImpls.CourseServiceImpl;
import org.example.courzelo.serviceImpls.GroupServiceImpl;
import org.example.courzelo.serviceImpls.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ElementModuleService {
    private final ElementModuleRepo elementModuleRepo;
    private final UserRepository userRepository;
    private final CourseServiceImpl courseService;
    private final UserServiceImpl userService;
    private final GroupRepository groupRepository;
    private final CourseRepository courseRepository;
    public List<User> getProfsByRole() {
        return userRepository.findUsersByRoles(Collections.singletonList(Role.TEACHER));
    }
    private List<ElementModule>getAllElementModules(){
        return elementModuleRepo.findAll();
    }
    public List<ElementModule> getEmploisByClasse(String id) {
        return elementModuleRepo.getElementModulesByGroup(id);
    }

    public void addElementModule(ElementModule elementDeModule) {
        elementModuleRepo.save(elementDeModule);
    }
    public ElementModuleDTO createElementModule(ElementModuleDTO elementModuleDTO) {
        ElementModule elementModule = mapToEntity(elementModuleDTO);
        ElementModule savedElementModule = elementModuleRepo.save(elementModule);
        return mapToDTO(savedElementModule);
    }
    private ElementModule mapToEntity(ElementModuleDTO dto) {
        Course course = courseService.findCourseById(dto.getCourse());
        Group group = findGroupById(dto.getGroup());
        User teacher = userService.findUserById(dto.getTeacher());
        return ElementModule.builder()
                .id(dto.getId())
                .nmbrHours(dto.getNmbrHours())
                .name(dto.getName())
                .dayOfWeek(dto.getDayOfWeek())
                .period(dto.getPeriod())
                .semesters(dto.getSemesters())
                .teacherID(dto.getTeacher())
                .institutionID(dto.getInstitutionID())
                .students(dto.getStudents())
                .course(course)
                .group(group)
                .teacher(teacher)
                .build();
    }
    private ElementModuleDTO mapToDTO(ElementModule entity) {
        ElementModuleDTO dto = new ElementModuleDTO();
        dto.setId(entity.getId());
        dto.setNmbrHours(entity.getNmbrHours());
        dto.setName(entity.getName());
        dto.setDayOfWeek(entity.getDayOfWeek());
        dto.setPeriod(entity.getPeriod());
        dto.setSemesters(entity.getSemesters());
        dto.setTeacher(entity.getTeacherID());
        dto.setInstitutionID(entity.getInstitutionID());
        dto.setStudents(entity.getStudents());
        if (entity.getCourse() != null) {
            dto.setCourse(entity.getCourse().getId());
        }
        if (entity.getGroup() != null) {
            dto.setGroup(entity.getGroup().getId());
        }
        return dto;
    }
    //method added by Nour challouf
    public List<GroupResponse> getClasses() {
        List<Group> groups = groupRepository.findAll();
        List<GroupResponse> groupResponses = groups.stream()
                .map(group -> GroupResponse.builder()
                        .id(group.getId())
                        .name(group.getName())
                        .build())
                .collect(Collectors.toList());
        return groupResponses;
    }

    public ResponseEntity<GroupResponse> getMyClass1(Principal principal, String email) {
        String loggedInEmail = principal.getName();
        User user = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Group group = groupRepository.findById(user.getEducation().getGroupID())
                .orElseThrow(() -> new NoSuchElementException("Group not found"));
        List<SimplifiedCourseResponse> courses = group.getCourses().stream().map(courseID -> {
            Course course = courseRepository.findById(courseID)
                    .orElseThrow(() -> new NoSuchElementException("Course not found"));
            return SimplifiedCourseResponse.builder()
                    .courseID(course.getId())
                    .courseName(course.getName())
                    .build();
        }).toList();
        GroupResponse groupResponse = GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .students(group.getStudents())
                .courses(courses)
                .build();

        // Return the response
        return ResponseEntity.ok(groupResponse);
    }

    public Group findGroupById(String group) {
        return groupRepository.findById(group).orElseThrow(() -> new NoSuchElementException("Group not found"));
    }

    public List<GroupResponse> getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        List<GroupResponse> groupResponses = groups.stream()
                .map(group -> GroupResponse.builder()
                        .id(group.getId())
                        .name(group.getName())
                        .build())
                .collect(Collectors.toList());
        return groupResponses;
    }


    public List<CourseResponse> getAllCourses() {
        return courseService.findAll();
    }

    public List<User> getAllTeachers() {
        return userRepository.findUsersByRoles(Collections.singletonList(Role.TEACHER));
    }
}