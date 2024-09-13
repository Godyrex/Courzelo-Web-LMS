package org.example.courzelo.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.courzelo.models.CodeType;
import org.example.courzelo.models.CodeVerification;
import org.example.courzelo.models.institution.Course;
import org.example.courzelo.models.institution.Group;
import org.example.courzelo.models.institution.Institution;
import org.example.courzelo.models.Role;
import org.example.courzelo.models.User;
import org.example.courzelo.repositories.CourseRepository;
import org.example.courzelo.repositories.GroupRepository;
import org.example.courzelo.repositories.InstitutionRepository;
import org.example.courzelo.repositories.UserRepository;
import org.example.courzelo.serviceImpls.CodeVerificationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Slf4j
public class CustomAuthorization {
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final CodeVerificationService codeVerificationService;
    private final CourseRepository courseRepository;
    private final GroupRepository groupRepository;
    public boolean canAccessGroup(String groupID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findUserByEmail(userEmail);
        Group group = groupRepository.findById(groupID).orElseThrow(() -> new NoSuchElementException("Group not found"));
        return user.getEducation().getInstitutionID().equals(group.getInstitutionID());
    }
    public boolean canAccessInstitution(String institutionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findUserByEmail(userEmail);
        if (user == null || !user.getRoles().contains(Role.ADMIN)) {
            return false;
        }

        Institution institution = institutionRepository.findById(institutionId).orElse(null);
        if (institution == null) {
            return false;
        }

        return institution.getAdmins().stream().anyMatch(admin -> admin.equals(userEmail));
    }
    public boolean canAcceptInstitutionInvite(String code) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findUserByEmail(userEmail);
        if(user == null){
            return false;
        }
        CodeVerification codeVerification= codeVerificationService.verifyCode(code);
        return codeVerification != null && codeVerification.getEmail().equals(userEmail) && codeVerification.getCodeType().equals(CodeType.INSTITUTION_INVITATION);
    }
    public boolean canCreateCourse(String institutionID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findUserByEmail(userEmail);
        if(user != null && user.getRoles().contains(Role.SUPERADMIN)){
            return true;
        }
        Institution institution = institutionRepository.findById(institutionID).orElse(null);
        if (institution == null) {
            return false;
        }
        return institution.getTeachers().stream().anyMatch(teacher -> teacher.equals(userEmail));
    }
    public boolean canAccessCourse(String courseID) {
        log.info("Checking if user can access course");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findUserByEmail(userEmail);
        if(user != null && user.getRoles().contains(Role.SUPERADMIN)){
            log.info("User is superadmin");
            return true;
        }
        Course course= courseRepository.findById(courseID).orElse(null);
        if(course == null){
            log.info("Course not found");
            return false;
        }
        if(course.getGroup()!=null) {
            log.info("Course has group");
            Group group = groupRepository.findById(course.getGroup()).orElseThrow(() -> new NoSuchElementException("Group not found"));
            return course.getTeacher().equals(userEmail) || group.getStudents().stream().anyMatch(student -> student.equals(userEmail));
        }
        return course.getTeacher().equals(userEmail);
    }
}
