package org.example.courzelo.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.courzelo.exceptions.*;
import org.example.courzelo.models.*;
import org.example.courzelo.models.institution.*;
import org.example.courzelo.models.institution.Module;
import org.example.courzelo.repositories.*;
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
    private final QuizRepository quizRepository;
    private final InvitationRepository invitationRepository;
    private final ProgramRepository programRepository;
    private final ModuleRepository moduleRepository;
    private final GradeRepository gradeRepository;
    public boolean isAdminInInstitution(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findUserByEmail(userEmail);
        if(user != null && user.getEducation() != null && user.getEducation().getInstitutionID() != null){
            Institution institution = institutionRepository.findById(user.getEducation().getInstitutionID()).orElse(null);
            if(institution != null){
                return institution.getAdmins().contains(userEmail);
            }
            return false;
        }
        return false;
    }
    public boolean canAccessProgram(String programID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findUserByEmail(userEmail);
        if(user == null){
            return false;
        }
        if(user.getRoles().contains(Role.SUPERADMIN)){
            return true;
        }
        Program program = programRepository.findById(programID).
                orElseThrow(() -> new NoSuchElementException("Program not found"));
        if(user.getEducation()!= null)
        {
            return user.getEducation().getInstitutionID().equals(program.getInstitutionID());
        }
        return false;
    }
    public boolean canAccessModule(String moduleID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findUserByEmail(userEmail);
        if(user == null){
            return false;
        }
        if(user.getRoles().contains(Role.SUPERADMIN)){
            return true;
        }
        Module module = moduleRepository.findById(moduleID).orElseThrow(() -> new ModuleNotFoundException("Module not found"));
        return user.getEducation().getInstitutionID().equals(module.getInstitutionID());
    }
    public boolean canAccessGroup(String groupID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findUserByEmail(userEmail);
        Group group = groupRepository.findById(groupID).orElseThrow(() -> new GroupNotFoundException("Group not found"));
        return user.getEducation().getInstitutionID().equals(group.getInstitutionID()) || group.getStudents().contains(userEmail);
    }
    public boolean canAccessGrade(String gradeID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findUserByEmail(userEmail);
        Grade grade = gradeRepository.findById(gradeID).orElseThrow(() -> new GradeNotFoundException("Grade not found"));
        return user.getEducation().getInstitutionID().equals(grade.getInstitutionID()) || grade.getStudentEmail().equals(userEmail);
    }
    public boolean canAccessInvitation(String invitationID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findUserByEmail(userEmail);
        if(user == null || user.getEducation() == null || user.getEducation().getInstitutionID() == null){
            return false;
        }
        Invitation invitation = invitationRepository.findById(invitationID).orElseThrow(()->new InvitationNotFoundException("Invitation not found"));
        return invitation.getInstitutionID().equals(user.getEducation().getInstitutionID());
    }
    public boolean canAccessInstitution(String institutionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findUserByEmail(userEmail);
        if (user == null || !user.getRoles().contains(Role.ADMIN)) {
            return false;
        }
        if(user.getRoles().contains(Role.SUPERADMIN)){
            return true;
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
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException("User not found"));
        if(user.getRoles().contains(Role.SUPERADMIN)){
            return true;
        }
        Institution institution = institutionRepository.findById(institutionID).orElseThrow(() -> new InstitutionNotFoundException("Institution not found"));
        return institution.getAdmins().stream().anyMatch(admin -> admin.equals(userEmail));
    }
    public boolean canAccessCourse(String courseID) {
        log.info("Checking if user can access course");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException("User not found"));
        if(user.getEducation()==null){
            log.info("User has no education");
            return false;
        }
        if( user.getRoles().contains(Role.SUPERADMIN)){
            log.info("User is superadmin");
            return true;
        }
        Course course= courseRepository.findById(courseID).orElseThrow(() -> new CourseNotFoundException("Course not found"));
        if(course.getInstitutionID().equals(user.getEducation().getInstitutionID())){
            log.info("User is in institution");
            return true;
        }
        if(course.getGroup()!=null) {
            log.info("Course has group");
            Group group = groupRepository.findById(course.getGroup()).orElseThrow(() -> new GroupNotFoundException("Group not found"));
            return (course.getTeacher()!= null&&course.getTeacher().equals(userEmail)) || group.getStudents().stream().anyMatch(student -> student.equals(userEmail));
        }
        return course.getTeacher().equals(userEmail);
    }
    public boolean canAccessQuiz(String quizID) {
        Quiz quiz = quizRepository.findById(quizID).orElseThrow(()->new NoSuchElementException("Quiz not found"));
        if(quiz.getCourse()!=null){
            return canAccessCourse(quiz.getCourse());
        }else{
            return false;
        }
    }
}
