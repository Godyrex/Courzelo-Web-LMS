package org.example.courzelo.serviceImpls;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.courzelo.dto.requests.module.AssessmentRequest;
import org.example.courzelo.dto.requests.module.ModuleRequest;
import org.example.courzelo.dto.responses.module.ModuleResponse;
import org.example.courzelo.dto.responses.module.PaginatedModulesResponse;
import org.example.courzelo.exceptions.*;
import org.example.courzelo.models.institution.Assessment;
import org.example.courzelo.models.institution.Module;
import org.example.courzelo.models.institution.Program;
import org.example.courzelo.models.institution.Semester;
import org.example.courzelo.repositories.CourseRepository;
import org.example.courzelo.repositories.ModuleRepository;
import org.example.courzelo.repositories.ProgramRepository;
import org.example.courzelo.services.IModuleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
@Slf4j
public class ModuleServiceImpl implements IModuleService {
    private final ModuleRepository moduleRepository;
    private final ProgramRepository programRepository;
    private final CourseRepository courseRepository;
    @Override
    public ResponseEntity<HttpStatus> createModule(ModuleRequest moduleRequest) {
        if(moduleRequest.getName() == null || moduleRequest.getDescription() == null || moduleRequest.getProgram() == null) {
            throw new RequestNotValidException("Module name, description and program are required");
        }
        if(moduleRepository.existsByNameAndProgram(moduleRequest.getName(), moduleRequest.getProgram())) {
            throw new ModuleAlreadyExistsException("Module with name " + moduleRequest.getName() + " already exists");
        }
        log.info("Creating module");
        log.info("Module request: " + moduleRequest);
        Program program = programRepository.findById(moduleRequest.getProgram()).orElseThrow(() -> new ProgramNotFoundException("Program not found"));
        Module module = Module.builder()
                .name(moduleRequest.getName())
                .description(moduleRequest.getDescription())
                .program(moduleRequest.getProgram())
                .semester(moduleRequest.getSemester() != null  ? Semester.valueOf(moduleRequest.getSemester()) : null)
                .skills(moduleRequest.getSkills())
                .ScoreToPass(moduleRequest.getScoreToPass())
                .duration(moduleRequest.getDuration())
                .credit(moduleRequest.getCredit())
                .isFinished(false)
                .institutionID(program.getInstitutionID())
                .moduleParts(moduleRequest.getModuleParts())
                .build();
        moduleRepository.save(module);
        addModuleToProgram(program, module.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<HttpStatus> updateModule(String id, ModuleRequest moduleRequest) {
        log.info("Updating module info :" + moduleRequest);
        if(moduleRequest.getName() == null || moduleRequest.getDescription() == null) {
            throw new RequestNotValidException("Module name and description are required");
        }
        if(moduleRepository.existsByNameAndProgram(moduleRequest.getName(), moduleRequest.getProgram())) {
            throw new ModuleAlreadyExistsException("Module with name " + moduleRequest.getName() + " already exists");
        }
        Module module = moduleRepository.findById(id).orElseThrow(() -> new ModuleNotFoundException("Module not found"));
        module.setName(moduleRequest.getName());
        module.setSemester(moduleRequest.getSemester() != null ? Semester.valueOf(moduleRequest.getSemester()) : null);
        module.setSkills(moduleRequest.getSkills());
        module.setScoreToPass(moduleRequest.getScoreToPass() != null ? moduleRequest.getScoreToPass() : 0.0);
        module.setDuration(moduleRequest.getDuration());
        module.setDescription(moduleRequest.getDescription());
        module.setIsFinished(moduleRequest.getIsFinished());
        module.setCredit(moduleRequest.getCredit());
        module.setModuleParts(moduleRequest.getModuleParts());
        moduleRepository.save(module);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteModule(String id) {
        Module module = moduleRepository.findById(id).orElseThrow(() -> new ModuleNotFoundException("Module not found"));
        Program program = programRepository.findById(module.getProgram()).orElseThrow(() -> new ProgramAlreadyExistsException("Program not found"));
        removeModuleFromProgram(program, id);
        removeModuleFromCourses(id);
        moduleRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void removeModuleFromCourses(String id) {
        courseRepository.deleteAllByModule(id);
    }

    @Override
    public ResponseEntity<PaginatedModulesResponse> getModulesByProgram(int page, int size, String programID, String keyword) {
        log.info("Getting modules by program");
        log.info("Page: " + page + " Size: " + size + " ProgramID: " + programID + " Keyword: " + keyword);
        Pageable pageable = PageRequest.of(page, size);
        Page<Module> modulePage;
        if(keyword == null) {
            modulePage = moduleRepository.findAllByProgram(programID, pageable);
        } else {
            modulePage = moduleRepository.findAllByProgramAndNameContainingIgnoreCase(programID, keyword, pageable);
        }
        log.info(modulePage.toString());
        log.info("Returning modules by program");
        return new ResponseEntity<>(PaginatedModulesResponse.builder()
                .modules(modulePage.getContent().stream().map(
                        module -> ModuleResponse.builder()
                                .id(module.getId())
                                .name(module.getName())
                                .description(module.getDescription())
                                .semester(module.getSemester()!=null?module.getSemester().name():null)
                                .assessments(module.getAssessments())
                                .scoreToPass(module.getScoreToPass())
                                .skills(module.getSkills())
                                .duration(module.getDuration())
                                .credit(module.getCredit())
                                .isFinished(module.getIsFinished())
                                .program(module.getProgram())
                                .institutionID(module.getInstitutionID())
                                .moduleParts(module.getModuleParts())
                                .build()
                ).toList())
                .totalPages(modulePage.getTotalPages())
                .totalItems(modulePage.getTotalElements())
                .currentPage(modulePage.getNumber())
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ModuleResponse> getModuleById(String id) {
        Module module = moduleRepository.findById(id).orElseThrow(() -> new ModuleNotFoundException("Module not found"));
        return new ResponseEntity<>(ModuleResponse.builder()
                .id(module.getId())
                .name(module.getName())
                .description(module.getDescription())
                .semester(module.getSemester()!=null?module.getSemester().name():null)
                .skills(module.getSkills())
                .assessments(module.getAssessments())
                .scoreToPass(module.getScoreToPass())
                .duration(module.getDuration())
                .credit(module.getCredit())
                .isFinished(module.getIsFinished())
                .program(module.getProgram())
                .institutionID(module.getInstitutionID())
                .moduleParts(module.getModuleParts())
                .build(), HttpStatus.OK);
    }

    @Override
    public void deleteAllProgramModules(String programID) {
        Program program = programRepository.findById(programID).orElseThrow(() -> new ProgramNotFoundException("Program not found"));
        if (program.getModules() != null) {
            program.getModules().forEach(this::deleteModule);
            program.setModules(new ArrayList<>());
            programRepository.save(program);
        }
    }

    @Override
    public void addModuleToProgram(Program program, String moduleID) {
        if(program.getModules()==null)
        {
            program.setModules(new ArrayList<>());
        }
        if(!program.getModules().contains(moduleID))
        {
            program.getModules().add(moduleID);
            programRepository.save(program);
        }
    }

    @Override
    public void removeModuleFromProgram(Program program, String moduleID) {
        if (program.getModules() != null && program.getModules().contains(moduleID)) {
            program.getModules().remove(moduleID);
            programRepository.save(program);
        }
    }

    @Override
    public ResponseEntity<HttpStatus> createAssessment(String id,AssessmentRequest assessmentRequest) {
        log.info("Creating assessment");
        Module module = moduleRepository.findById(id).orElseThrow(() -> new ModuleNotFoundException("Module not found"));
        if(module.getAssessments() == null) {
            module.setAssessments(new ArrayList<>());
        }
        if(module.getAssessments().stream().anyMatch(assessment -> assessment.getName().equals(assessmentRequest.getName()))) {
            log.info("Assessment already exists");
            throw new AssessmentAlreadyExistsException("Assessment with name " + assessmentRequest.getName() + " already exists");
        }
//        if(module.getAssessments().stream().mapToDouble(Assessment::getWeight).sum() + assessmentRequest.getWeight() > 1) {
//            log.info("Sum of assessment weights exceeds 100%");
//            throw new AssessmentSumExceedsOneException("Sum of assessment weights exceeds 100%");
//        }
        module.getAssessments().add(Assessment.builder()
                .name(assessmentRequest.getName())
                .weight(assessmentRequest.getWeight()/100)
                .build());
        moduleRepository.save(module);
        log.info("Assessment created");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteAssessment(String id, String assessmentName) {
        Module module = moduleRepository.findById(id).orElseThrow(() -> new ModuleNotFoundException("Module not found"));
        if(module.getAssessments() == null || module.getAssessments().stream().noneMatch(assessment -> assessment.getName().equals(assessmentName))) {
            throw new AssessmentNotFoundException("Assessment not found");
        }
        module.getAssessments().removeIf(assessment -> assessment.getName().equals(assessmentName));
        moduleRepository.save(module);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> updateAssessment(String id, AssessmentRequest assessmentRequest) {
        Module module = moduleRepository.findById(id).orElseThrow(() -> new ModuleNotFoundException("Module not found"));
        if(module.getAssessments() == null || module.getAssessments().stream().noneMatch(assessment -> assessment.getName().equals(assessmentRequest.getOldName()))) {
            throw new AssessmentNotFoundException("Assessment not found");
        }
//        double currentTotalWeight = module.getAssessments().stream().mapToDouble(Assessment::getWeight).sum();
//        double oldWeight = module.getAssessments().stream()
//                .filter(assessment -> assessment.getName().equals(assessmentRequest.getOldName()))
//                .mapToDouble(Assessment::getWeight)
//                .findFirst()
//                .orElse(0.0);
//        if(currentTotalWeight - oldWeight + assessmentRequest.getWeight() > 1) {
//            throw new AssessmentSumExceedsOneException("Sum of assessment weights exceeds 100%");
//        }
        module.getAssessments().removeIf(assessment -> assessment.getName().equals(assessmentRequest.getOldName()));
        module.getAssessments().add(Assessment.builder()
                .name(assessmentRequest.getName())
                .weight(assessmentRequest.getWeight()/100)
                .build());
        moduleRepository.save(module);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
