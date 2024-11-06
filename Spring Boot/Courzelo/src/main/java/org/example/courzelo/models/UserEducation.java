package org.example.courzelo.models;

import lombok.Data;
import org.example.courzelo.dto.responses.institution.InstitutionTimeSlot;
import org.example.courzelo.models.Timetable.ElementModule;
import org.example.courzelo.models.institution.Timeslot;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserEducation {
    private String institutionID;
    private List<String> coursesID = new ArrayList<>();
    private String groupID;
    private List<String> skill = new ArrayList<>();
    private List<String> grades = new ArrayList<>();
    private List<InstitutionTimeSlot> disponibilitySlots = new ArrayList<>();
    private List<ElementModule>elementModules = new ArrayList<>();
}
