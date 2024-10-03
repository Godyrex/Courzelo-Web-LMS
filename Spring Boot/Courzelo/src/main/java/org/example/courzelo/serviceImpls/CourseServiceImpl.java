package org.example.courzelo.serviceImpls;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.courzelo.dto.QuizDTO;
import org.example.courzelo.dto.requests.CoursePostRequest;
import org.example.courzelo.dto.requests.CourseRequest;
import org.example.courzelo.dto.responses.CoursePostResponse;
import org.example.courzelo.dto.responses.CourseResponse;
import org.example.courzelo.exceptions.*;
import org.example.courzelo.models.User;
import org.example.courzelo.models.institution.Course;
import org.example.courzelo.models.institution.CoursePost;
import org.example.courzelo.models.institution.Group;
import org.example.courzelo.models.institution.Institution;
import org.example.courzelo.repositories.*;
import org.example.courzelo.services.ICourseService;
import org.example.courzelo.services.QuizService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class CourseServiceImpl implements ICourseService {
    private final CourseRepository courseRepository;
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final QuizService quizService;
    @Override
    public ResponseEntity<HttpStatus> createCourse(String institutionID, CourseRequest courseRequest,Principal principal) {
        log.info("Creating course");
        Institution institution = institutionRepository.findById(institutionID).orElseThrow();
        Course course = Course.builder()
                .name(courseRequest.getName())
                .description(courseRequest.getDescription())
                .credit(courseRequest.getCredit())
                .module(courseRequest.getModule())
                .institutionID(institution.getId())
                .teacher(courseRequest.getTeacher())
                .group(courseRequest.getGroup())
                .posts(new ArrayList<>())
                .build();
        courseRepository.save(course);
        institution.getCoursesID().add(course.getId());
        institutionRepository.save(institution);
        if(courseRequest.getTeacher()!=null){
            log.info("Adding course to teacher");
            if (institution.getTeachers().contains(courseRequest.getTeacher())) {
                User teacher = userRepository.findUserByEmail(courseRequest.getTeacher());
                teacher.getEducation().getCoursesID().add(course.getId());
                userRepository.save(teacher);
            }
        }
        if(courseRequest.getGroup()!=null){
            log.info("Adding course to group");
            Group group = groupRepository.findById(courseRequest.getGroup()).orElseThrow(() -> new GroupNotFoundException("Group not found"));
            group.getCourses().add(course.getId());
            groupRepository.save(group);
            log.info("Course added to group");
        }
        log.info("Course created");
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> updateCourse(String courseID, CourseRequest courseRequest) {
        Course course = courseRepository.findById(courseID).orElseThrow();
        course.setName(courseRequest.getName());
        course.setDescription(courseRequest.getDescription());
        if (!course.getTeacher().equals(courseRequest.getTeacher())) {
            Institution institution = institutionRepository.findById(course.getInstitutionID()).orElseThrow();
            if (institution.getTeachers().contains(courseRequest.getTeacher())) {
                userRepository.findByEmail(courseRequest.getTeacher()).ifPresent(
                        teacher -> {
                            course.setTeacher(courseRequest.getTeacher());
                            if (teacher.getEducation() != null && teacher.getEducation().getCoursesID() != null && !teacher.getEducation().getCoursesID().contains(course.getId())) {
                                teacher.getEducation().getCoursesID().add(course.getId());
                                userRepository.save(teacher);
                            }
                        }
                );
            }
        }
        course.setCredit(courseRequest.getCredit());
        courseRepository.save(course);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteCourse(String courseID) {
        log.info("Deleting course with id: " + courseID);
        Course course = courseRepository.findById(courseID).orElseThrow(() -> new CourseNotFoundException("Course not found"));
        if (course.getGroup() != null) {
            log.info("Deleting course from group");
            groupRepository.findById(course.getGroup()).ifPresent(group -> {
                group.getCourses().remove(course.getId());
                groupRepository.save(group);
            });
        }
        if(course.getTeacher()!= null) {
            log.info("Deleting course from teacher");
             userRepository.findByEmail(course.getTeacher()).ifPresent(teacher -> {
                 if (teacher.getEducation() != null && teacher.getEducation().getCoursesID() != null && teacher.getEducation().getCoursesID().contains(course.getId())) {
                     teacher.getEducation().getCoursesID().remove(course.getId());
                     userRepository.save(teacher);
                 }
             });
        }
         institutionRepository.findById(course.getInstitutionID()).ifPresent(
                institution -> {
                    if (institution.getCoursesID() != null && institution.getCoursesID().contains(course.getId())) {
                        institution.getCoursesID().remove(course.getId());
                        institutionRepository.save(institution);
                    }
                }
         );
        courseRepository.delete(course);
        log.info("Course deleted");
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CourseResponse> getCourse(String courseID) {
        Course course = courseRepository.findById(courseID).orElseThrow(() -> new CourseNotFoundException("Course not found"));
        List<QuizDTO> quizzes = new ArrayList<>();
        if(course.getQuizzes()!= null){
            course.getQuizzes().forEach(quizID -> {
                QuizDTO quiz = quizService.getQuizById(quizID);
                quizzes.add(quiz);
            });
            }
        return ResponseEntity.ok(CourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .module(course.getModule())
                .credit(course.getCredit())
                .teacher(course.getTeacher())
                .group(course.getGroup())
                .posts(course.getPosts() != null ? course.getPosts().stream().map(coursePost -> CoursePostResponse.builder()
                        .id(coursePost.getId())
                        .title(coursePost.getTitle())
                        .description(coursePost.getDescription())
                        .created(coursePost.getCreated())
                        .files(coursePost.getFiles() != null ? returnOnlyFileName(coursePost.getFiles()) : null)
                        .build()).toList() : List.of())
                        .institutionID(course.getInstitutionID())
                        .quizzes(quizzes)
                .build());
    }

    @Override
    public ResponseEntity<HttpStatus> setTeacher(String courseID, String email) {
        Course course = courseRepository.findById(courseID).orElseThrow(()-> new CourseNotFoundException("Course not found"));
        Institution institution = institutionRepository.findById(course.getInstitutionID()).orElseThrow(() -> new InstitutionNotFoundException("Institution not found"));
        if(course.getTeacher().equals(email)){
            throw new TeacherAlreadyAssignedException("Teacher already assigned to course");
        }
        if(institution.getTeachers().contains(email)){
            User teacher = userRepository.findUserByEmail(email);
            course.setTeacher(teacher.getEmail());
            teacher.getEducation().getCoursesID().add(course.getId());
            userRepository.save(teacher);
            courseRepository.save(course);
            return ResponseEntity.ok(HttpStatus.OK);
        }
        throw new UserNotPartOfInstitutionException(email+" is not part of "+institution.getName());
    }
    public List<String> returnOnlyFileName(List<String> files) {
        log.info("Returning only file name {}", files);
        return files.stream()
                .map(file -> file.replace("\\", "/"))
                .map(file -> file.substring(file.lastIndexOf('/') + 1))
                .toList();
    }

    @Override
    public ResponseEntity<HttpStatus> addPost(String courseID, CoursePostRequest coursePostRequest, MultipartFile[] files) {
        log.info("Adding post to course");
        log.info("Course ID: " + courseID);
        log.info("Title: " + coursePostRequest.getTitle());
        log.info("Description: " + coursePostRequest.getDescription());
        log.info("Files: " + Arrays.toString(files));
        Course course = courseRepository.findById(courseID).orElseThrow(() -> new CourseNotFoundException("Course not found"));
        Institution institution = institutionRepository.findById(course.getInstitutionID()).orElseThrow(() -> new InstitutionNotFoundException("Institution not found"));
        if(course.getPosts()==null){
            course.setPosts(new ArrayList<>());
        }
        course.getPosts().add(CoursePost.builder()
                .title(coursePostRequest.getTitle())
                .description(coursePostRequest.getDescription())
                .created(LocalDateTime.now())
                .files(uploadFiles(files,course.getId(),institution))
                .build());
        log.info("Post added to course");
        courseRepository.save(course);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deletePost(String courseID, String postID) {
        Course course = courseRepository.findById(courseID).orElseThrow(() -> new CourseNotFoundException("Course not found"));
        //delete files
        course.getPosts().stream().filter(coursePost -> coursePost.getId().equals(postID)).findFirst().ifPresent(coursePost -> {
            coursePost.getFiles().forEach(file -> {
                try {
                    Files.delete(new File(file).toPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        course.getPosts().removeIf(coursePost -> coursePost.getId().equals(postID));
        courseRepository.save(course);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @Override
    public void removeTeacherFromCourses(String teacherEmail) {
        List<Course> courses = courseRepository.findAllByTeacher(teacherEmail).orElseThrow(() -> new CourseNotFoundException("Courses not found"));
        User teacher = userRepository.findByEmail(teacherEmail).orElseThrow(() -> new UserNotFoundException("Teacher not found"));
        courses.forEach(course -> {
            course.setTeacher(null);
            teacher.getEducation().getCoursesID().remove(course.getId());
            userRepository.save(teacher);
            courseRepository.save(course);
        });
    }

    @Override
    public ResponseEntity<byte[]> downloadFile(String courseID, String fileName) {
        log.info("Downloading file {}", fileName);
        Course course = courseRepository.findById(courseID).orElseThrow();
        List<CoursePost> posts = course.getPosts();
        for (CoursePost post : posts) {
            for (String filePath : post.getFiles()) {
                String normalizedFilePath = filePath.replace("\\", "/");
                String extractedFileName = normalizedFilePath.substring(normalizedFilePath.lastIndexOf('/') + 1);
                if (extractedFileName.equals(fileName)) {
                    log.info("File found");
                    try {
                        return ResponseEntity.ok(Files.readAllBytes(new File(normalizedFilePath).toPath()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    private List<byte[]> getBytesFromFiles(List<String> files) {
        return files.stream().map(file -> {
            try {
                return Files.readAllBytes(new File(file).toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
    private List<String> uploadFiles(MultipartFile[] files,String courseID,Institution institution) {
        log.info("Uploading files");
        String baseDir = "upload" + File.separator + institution.getId()+ File.separator +courseID  + File.separator;
        return Stream.of(files).map(file -> {
            try {
                File dir = new File(baseDir);
                if (!dir.exists()) {
                    boolean dirsCreated = dir.mkdirs();
                    if (!dirsCreated) {
                        throw new IOException("Failed to create directories");
                    }
                }
                String originalFileName = file.getOriginalFilename();
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                // Generate a random filename
                String newFileName = UUID.randomUUID() + extension;
                // Define the path to the new file
                String filePath = baseDir + newFileName;
                log.info("File path: " + filePath);
                Files.copy(file.getInputStream(), new File(filePath).toPath());
                log.info("File uploaded");
                return filePath;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
    //getallcourses:addedByNourChallouf
    public List<CourseResponse>findAll() {
        return courseRepository.findAll(Sort.by("id")).stream().map(course -> CourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .module(course.getModule())
                .credit(course.getCredit())
                .teacher(course.getTeacher())
                .group(course.getGroup())
                .posts(course.getPosts() != null ? course.getPosts().stream().map(coursePost -> CoursePostResponse.builder()
                        .id(coursePost.getId())
                        .title(coursePost.getTitle())
                        .description(coursePost.getDescription())
                        .created(coursePost.getCreated())
                        .files(coursePost.getFiles() != null ? returnOnlyFileName(coursePost.getFiles()) : null)
                        .build()).toList() : List.of())
                .institutionID(course.getInstitutionID())
                .build()).toList();
    }

    public Course findCourseById(String id) {
        return courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException("Course not found"));
    }
}
