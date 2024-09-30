package org.example.courzelo.serviceImpls;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.courzelo.dto.requests.GroupRequest;
import org.example.courzelo.dto.responses.GroupResponse;
import org.example.courzelo.dto.responses.PaginatedGroupsResponse;
import org.example.courzelo.dto.responses.institution.SimplifiedCourseResponse;
import org.example.courzelo.models.Role;
import org.example.courzelo.models.User;
import org.example.courzelo.models.institution.Course;
import org.example.courzelo.models.institution.Group;
import org.example.courzelo.models.institution.Institution;
import org.example.courzelo.repositories.*;
import org.example.courzelo.services.IGroupService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class GroupServiceImpl implements IGroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final InstitutionRepository institutionRepository;
    private final MongoTemplate mongoTemplate;
    private final ProgramRepository programRepository;
    @Override
    public ResponseEntity<GroupResponse> getGroup(String groupID) {
        Group group = groupRepository.findById(groupID).orElseThrow(() -> new NoSuchElementException("Group not found"));
        return ResponseEntity.ok(GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .students(group.getStudents())
                .institutionID(group.getInstitutionID())
                .courses(group.getCourses().stream().map(
                        courseID -> {
                            Course course = courseRepository.findById(courseID).orElseThrow(() -> new NoSuchElementException("Course not found"));
                            return SimplifiedCourseResponse.builder()
                                    .courseID(course.getId())
                                    .courseName(course.getName())
                                    .build();
                        }
                        ).toList()
                )
                .program(group.getProgram()!=null?group.getProgram():null)
                .build());
    }

    @Override
    public ResponseEntity<PaginatedGroupsResponse> getGroupsByInstitution(String institutionID, int page,String keyword ,int sizePerPage) {
        log.info("Fetching groups by institution {} page {} sizer per page {} keyword {}", institutionID, page, sizePerPage, keyword);
        Pageable pageable = PageRequest.of(page, sizePerPage);
        Page<Group> groupPage;
        if(keyword == null) {
            groupPage = groupRepository.findByInstitutionID(institutionID, pageable);
        } else {
            groupPage = groupRepository.findByInstitutionIDAndNameContainingIgnoreCase(institutionID, keyword, pageable);
        }
        List<Group> groups = groupPage.getContent().stream().toList();

        List<GroupResponse> groupResponses = groups.stream()
                .map(group -> GroupResponse.builder()
                        .id(group.getId())
                        .name(group.getName())
                        .institutionID(group.getInstitutionID())
                        .students(group.getStudents())
                        .courses(group.getCourses().stream().map(
                                        courseID -> {
                                            Course course = courseRepository.findById(courseID).orElseThrow(() -> new NoSuchElementException("Course not found"));
                                            return SimplifiedCourseResponse.builder()
                                                    .courseID(course.getId())
                                                    .courseName(course.getName())
                                                    .build();
                                        }
                                ).toList()
                        )
                        .program(group.getProgram()!=null?group.getProgram():null)
                        .build())
                .collect(Collectors.toList());

        log.info("Groups fetched successfully {}", groupResponses);

        return ResponseEntity.ok(PaginatedGroupsResponse.builder()
                .groups(groupResponses)
                .currentPage(page)
                .totalPages(groupPage.getTotalPages())
                .totalItems(groupPage.getTotalElements())
                        .itemsPerPage(sizePerPage)
                .build());
    }

    @Override
    public ResponseEntity<HttpStatus> createGroup(GroupRequest groupRequest) {
        log.info("Creating group {}", groupRequest);
        if (groupRequest.getStudents() != null) {
            List<String> students = new ArrayList<>(groupRequest.getStudents());
            students.forEach(
                    studentEmail -> {
                        if (!checkIfUserCanBeAddedToGroup(studentEmail, groupRequest.getInstitutionID())) {
                            log.info("User {} cannot be added to group", studentEmail);
                            groupRequest.getStudents().remove(studentEmail);
                        }
                    }
            );
        }
        log.info("Creating group");
        Group group = Group.builder()
                    .name(groupRequest.getName())
                    .institutionID(groupRequest.getInstitutionID())
                    .students(groupRequest.getStudents()!=null ? groupRequest.getStudents() : new ArrayList<>())
                    .courses(new ArrayList<>())
                    .program(groupRequest.getProgram()!=null ? groupRequest.getProgram() : null)
                    .build();
            groupRepository.save(group);
            log.info("Group created");
            if (groupRequest.getStudents() != null) {
                groupRequest.getStudents().forEach(
                        studentEmail -> addGroupToUser(group.getId(), studentEmail)
                );
            }
            addGroupToInstitution(group.getId(), groupRequest.getInstitutionID());
            if(groupRequest.getProgram()!=null){
                programRepository.findById(groupRequest.getProgram()).ifPresent(program -> {
                    if(program.getGroups()==null){
                        program.setGroups(new ArrayList<>());
                    }
                    program.getGroups().add(group.getId());
                    programRepository.save(program);
                });
            }

        return ResponseEntity.ok(HttpStatus.CREATED);

    }
    private boolean checkIfUserCanBeAddedToGroup(String userEmail, String institutionID) {
        log.info("Checking if user {} can be added to group", userEmail);
        User user = userRepository.findByEmail(userEmail).orElse(null);

        if (user == null) {
            log.info("User {} not found", userEmail);
            return false;
        }

        if (!user.getRoles().contains(Role.STUDENT)) {
            log.info("User {} does not have the STUDENT role", userEmail);
            return false;
        }

        if (user.getEducation() == null) {
            log.info("User {} does not have education information", userEmail);
            return false;
        }

        if (user.getEducation().getInstitutionID() == null) {
            log.info("User {} does not have an institution ID", userEmail);
            return false;
        }

        if (!user.getEducation().getInstitutionID().equals(institutionID)) {
            log.info("User {} is not in the institution {}", userEmail, institutionID);
            return false;
        }

        log.info("User {} can be added to group", userEmail);
        return true;
    }
    private void addGroupToUser(String groupID, String userEmail) {
        log.info("Adding group to user {}", userEmail);
        userRepository.findByEmail(userEmail).ifPresent(user -> {
            if(user.getEducation().getGroupID()!=null)
            {
                removeStudentFromGroup(user.getEducation().getGroupID(), userEmail);
            }
            user.getEducation().setGroupID(groupID);
            userRepository.save(user);
        });
    }
    private void addGroupToInstitution(String groupID, String institutionID) {
        institutionRepository.findById(institutionID).ifPresent(institution -> {
            institution.getGroupsID().add(groupID);
            institutionRepository.save(institution);
        });
    }
    private void addGroupToCourse(String groupID, String courseID) {
        courseRepository.findById(courseID).ifPresent(course -> {
            course.setGroup(groupID);
            courseRepository.save(course);
        });
    }
    @Override
    public ResponseEntity<HttpStatus> updateGroup(String groupID, GroupRequest groupRequest) {
        Group group = groupRepository.findById(groupID).orElseThrow(() -> new NoSuchElementException("Group not found"));
        group.setName(groupRequest.getName());
        if (groupRequest.getStudents() != null) {
            List<String> students = new ArrayList<>(groupRequest.getStudents());
            List<String> studentsToRemove = new ArrayList<>();
            group.getStudents().forEach(
                    studentEmail -> {
                        if (!students.contains(studentEmail)) {
                            studentsToRemove.add(studentEmail);
                        }
                    }
            );
            students.forEach(
                    studentEmail -> {
                        if (!checkIfUserCanBeAddedToGroup(studentEmail, group.getInstitutionID())) {
                            log.info("User {} cannot be added to group", studentEmail);
                            groupRequest.getStudents().remove(studentEmail);
                        }
                    }
            );
            group.setStudents(groupRequest.getStudents());
                groupRequest.getStudents().forEach(
                        studentEmail -> addGroupToUser(group.getId(), studentEmail)
                );
                if(!studentsToRemove.isEmpty()){
                    studentsToRemove.forEach(
                            studentEmail -> removeStudentFromGroup(group.getId(), studentEmail)
                    );
                }
        }


        if(groupRequest.getProgram()!=null && groupRequest.getProgram().equals(group.getProgram())){
            programRepository.findById(groupRequest.getProgram()).ifPresent(program -> {
                group.setProgram(program.getId());
                if(program.getGroups()==null){
                    program.setGroups(new ArrayList<>());
                }
                program.getGroups().add(groupID);
                programRepository.save(program);
            });
        }
        groupRepository.save(group);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteGroup(String groupID) {
        Group group = groupRepository.findById(groupID).orElseThrow(() -> new NoSuchElementException("Group not found"));
        if (group.getStudents() != null) {
            log.info("Removing group from students");
            group.getStudents().forEach(
                    this::removeGroupFromUser
            );
        }
        if (group.getCourses() != null) {
            log.info("Removing group from courses");
            group.getCourses().forEach(
                    this::removeGroupFromCourse
            );
        }
        Institution institution = institutionRepository.findById(group.getInstitutionID()).orElseThrow(() -> new NoSuchElementException("Institution not found"));
        if (institution.getGroupsID() != null) {
            institution.getGroupsID().remove(groupID);
            institutionRepository.save(institution);
        }
        if(group.getProgram()!=null){
            programRepository.findById(group.getProgram()).ifPresent(program -> {
                if (program.getGroups() != null) {
                    program.getGroups().remove(groupID);
                    programRepository.save(program);
                }
            });
        }
        groupRepository.delete(group);
        log.info("Group deleted");
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private void removeGroupFromUser(String studentEmail) {
        userRepository.findByEmail(studentEmail).ifPresent(user -> {
            user.getEducation().setGroupID(null);
            userRepository.save(user);
        });
    }
    private void removeGroupFromCourse(String courseID) {
        courseRepository.findById(courseID).ifPresent(course -> {
            course.setGroup(null);
            courseRepository.save(course);
        });
    }

    @Override
    public ResponseEntity<HttpStatus> addStudentToGroup(String groupID, String student) {
        Group group = groupRepository.findById(groupID).orElseThrow(() -> new NoSuchElementException("Group not found"));
        if(group.getStudents().contains(student)) {
            return ResponseEntity.ok(HttpStatus.OK);
        }
        log.info("Adding user {} to group {}", student, groupID);
        User user = userRepository.findByEmail(student).orElseThrow(() -> new NoSuchElementException("User not found"));
        if(!Objects.equals(user.getEducation().getInstitutionID(), group.getInstitutionID()))
        {
            log.info("User {} is not in the same institution as group {}", student, groupID);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        group.getStudents().add(user.getEmail());
        user.getEducation().setGroupID(groupID);
        groupRepository.save(group);
        userRepository.save(user);
        log.info("User {} added to group {}", student, groupID);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public void removeStudentFromGroup(String groupID, String studentID) {
        log.info("Removing user {} from group {}", studentID, groupID);
        User user = userRepository.findByEmail(studentID).orElse(null);
        Group group = groupRepository.findById(groupID).orElseThrow(() -> new NoSuchElementException("Group not found"));
        if (user != null && group.getStudents() != null && group.getStudents().contains(user.getEmail())) {
            log.info("Removing user {} from group {}", studentID, groupID);
            group.getStudents().remove(user.getEmail());
            user.getEducation().setGroupID(null);
            groupRepository.save(group);
            userRepository.save(user);
        }

    }

    @Override
    public void deleteGroupsByInstitution(String institutionID) {
        Institution institution = institutionRepository.findById(institutionID).orElseThrow(() -> new NoSuchElementException("Institution not found"));
        institution.getGroupsID().forEach(
                this::deleteGroup
        );
    }

    @Override
    public void removeStudentFromAllGroups(User user) {
        if (user.getEducation() != null && user.getEducation().getGroupID() != null) {
            Group group = groupRepository.findById(user.getEducation().getGroupID()).orElseThrow(() -> new NoSuchElementException("Group not found"));
            group.getStudents().remove(user.getEmail());
            user.getEducation().setGroupID(null);
            userRepository.save(user);
            groupRepository.save(group);
        }
    }
}
