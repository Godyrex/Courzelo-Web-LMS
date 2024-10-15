package org.example.courzelo.serviceImpls;

import lombok.Getter;
import org.example.courzelo.exceptions.InstitutionNotFoundException;
import org.example.courzelo.exceptions.ModuleNotFoundException;
import org.example.courzelo.exceptions.UserNotFoundException;
import org.example.courzelo.models.User;
import org.example.courzelo.models.institution.Course;
import org.example.courzelo.models.institution.Group;
import org.example.courzelo.models.institution.Timeslot;
import org.example.courzelo.repositories.GroupRepository;
import org.example.courzelo.repositories.InstitutionRepository;
import org.example.courzelo.repositories.ModuleRepository;
import org.example.courzelo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class TimetableService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private GroupRepository groupRepository;
    private List<Timeslot> availableTimeslots;
    // Getter methods for the generated timetables
    @Getter
    private Map<String, List<Timeslot>> groupTimetables; // groupId -> List of timeslots assigned
    @Getter
    private Map<String, List<Timeslot>> teacherTimetables; // teacherId -> List of timeslots assigned

    public TimetableService() {
        initializeTimeslots();
        groupTimetables = new HashMap<>();
        teacherTimetables = new HashMap<>();
    }

    // Initialize available timeslots for the week (example times)
    private void initializeTimeslots() {
        availableTimeslots = new ArrayList<>();
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] timeSlots = {"09:00-11:00", "11:00-13:00", "13:00-15:00", "15:00-17:00"};

        for (String day : daysOfWeek) {
            for (String slot : timeSlots) {
                String[] times = slot.split("-");
                availableTimeslots.add(new Timeslot(day, times[0], times[1]));
            }
        }
    }
    // Fetch all groups by institution and initialize groupTimetables
    public void initializeGroupTimetables(String institutionId) {
        List<Group> groups = groupRepository.findByInstitutionID(institutionId).orElseThrow(()-> new InstitutionNotFoundException("Institution doesn't exist"));
        for (Group group : groups) {
            groupTimetables.put(group.getName(), new ArrayList<>());
        }
    }
    // Generate the timetable for all groups and teachers
    public void generateTimetable(List<Course> courses,String institutionID) {
        // Sort courses based on teacherId (or email), but place courses with null teachers first
        courses.sort(Comparator.comparing(Course::getTeacher, Comparator.nullsFirst(Comparator.naturalOrder())));
        initializeGroupTimetables(institutionID);
        // Iterate through each course and assign to a timeslot
        for (Course course : courses) {

            String groupName = groupRepository.findById(course.getGroup()).orElseThrow(()-> new UserNotFoundException("Group doesn't exist")).getName();
            String teacherFullName = null; // May be null
            if (course.getTeacher() != null) {
                User user = userRepository.findByEmail(course.getTeacher()).orElseThrow(()-> new UserNotFoundException("Teacher doesn't exist"));
                teacherFullName = user.getProfile().getName()+' '+user.getProfile().getLastname();
            }

            // Assign a timeslot for this course (groupId and teacherId)
            Timeslot assignedSlot = assignTimeslot(groupName, teacherFullName);

            if (assignedSlot != null) {
                // Save the assigned timeslot in the group's timetable
                assignedSlot.setTeacher(teacherFullName != null ? teacherFullName : "Teacher not assigned");
                assignedSlot.setModule(moduleRepository.findById(course.getModule()).orElseThrow(()-> new ModuleNotFoundException("Module Not Found")).getName());
                groupTimetables.computeIfAbsent(groupName, k -> new ArrayList<>()).add(assignedSlot);

                // Only update teacher timetable if a teacher is assigned
                if (teacherFullName != null && !teacherFullName.equals("Teacher not assigned")) {
                    assignedSlot.setGroup(groupName);
                    teacherTimetables.computeIfAbsent(teacherFullName, k -> new ArrayList<>()).add(assignedSlot);
                }
            } else {
                System.out.println("Could not assign timeslot for course: " + course.getModule());
            }
        }
    }


    // Assign a free timeslot for a course considering both group and teacher availability
    private Timeslot assignTimeslot(String groupName, String teacherFullName) {
        for (Timeslot slot : availableTimeslots) {
            if (isAvailableForGroup(groupName, slot) && isAvailableForTeacher(teacherFullName, slot)) {
                return slot; // First available slot found
            }
        }
        return null; // No available timeslot
    }

    // Check if the group is available for the timeslot
    private boolean isAvailableForGroup(String groupName, Timeslot slot) {
        return !groupTimetables.containsKey(groupName) || !groupTimetables.get(groupName).contains(slot);
    }

    // Check if the teacher is available for the timeslot
    private boolean isAvailableForTeacher(String teacherFullName, Timeslot slot) {
        return !teacherTimetables.containsKey(teacherFullName) || !teacherTimetables.get(teacherFullName).contains(slot);
    }

}

