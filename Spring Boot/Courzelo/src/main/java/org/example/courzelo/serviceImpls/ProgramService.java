package org.example.courzelo.serviceImpls;

import lombok.AllArgsConstructor;
import org.example.courzelo.dto.requests.program.ProgramRequest;
import org.example.courzelo.dto.responses.program.PaginatedProgramsResponse;
import org.example.courzelo.dto.responses.program.ProgramResponse;
import org.example.courzelo.models.User;
import org.example.courzelo.models.institution.Institution;
import org.example.courzelo.models.institution.Program;
import org.example.courzelo.repositories.InstitutionRepository;
import org.example.courzelo.repositories.ProgramRepository;
import org.example.courzelo.repositories.UserRepository;
import org.example.courzelo.services.IModuleService;
import org.example.courzelo.services.IProgramService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ProgramService implements IProgramService {
    private final ProgramRepository programRepository;
    private final UserRepository userRepository;
    private final IModuleService moduleService;
    private final InstitutionRepository institutionRepository;
    @Override
    public ResponseEntity<HttpStatus> createProgram(ProgramRequest programRequest, Principal principal) {
        if(programRequest.getName() == null || programRequest.getDescription() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        if(user.getEducation() == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if(programRepository.existsByNameAndInstitutionID(programRequest.getName(), user.getEducation().getInstitutionID())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Program program = Program.builder()
                .name(programRequest.getName())
                .description(programRequest.getDescription())
                .institutionID(user.getEducation().getInstitutionID())
                .build();
        programRepository.save(program);
        addProgramToInstitution(program.getId(), user.getEducation().getInstitutionID());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<HttpStatus> updateProgram(String id, ProgramRequest programRequest) {
        Program program = programRepository.findById(id).orElseThrow(() -> new RuntimeException("Program not found"));
        program.setName(programRequest.getName());
        program.setDescription(programRequest.getDescription());
        programRepository.save(program);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteProgram(String id) {
        Program program = programRepository.findById(id).orElseThrow(() -> new RuntimeException("Program not found"));
        removeProgramFromInstitution(program.getId(), program.getInstitutionID());
        moduleService.deleteAllProgramModules(program.getId());
        programRepository.delete(program);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PaginatedProgramsResponse> getProgramsByInstitution(int page, int size, String institutionID, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Program> programPage;
        if(keyword == null) {
            programPage = programRepository.findAllByInstitutionID(institutionID, pageable);
        } else {
            programPage = programRepository.findAllByInstitutionIDAndNameContainingIgnoreCase(institutionID, keyword, pageable);
        }
        PaginatedProgramsResponse paginatedProgramsResponse = PaginatedProgramsResponse.builder()
                .programs(programPage.getContent().stream().map(
                        program -> ProgramResponse.builder()
                                .id(program.getId())
                                .name(program.getName())
                                .description(program.getDescription())
                                .institutionID(program.getInstitutionID())
                                .build()
                ).toList()
                )
                .totalPages(programPage.getTotalPages())
                .totalItems(programPage.getTotalElements())
                .currentPage(programPage.getNumber())
                .build();
        return new ResponseEntity<>(paginatedProgramsResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ProgramResponse> getProgramById(String id) {
        Program program = programRepository.findById(id).orElseThrow(() -> new RuntimeException("Program not found"));
        ProgramResponse programResponse = ProgramResponse.builder()
                .id(program.getId())
                .name(program.getName())
                .description(program.getDescription())
                .institutionID(program.getInstitutionID())
                .build();
        return new ResponseEntity<>(programResponse, HttpStatus.OK);
    }

    @Override
    public void deleteAllInstitutionPrograms(String institutionID) {
        Institution institution = institutionRepository.findById(institutionID).orElseThrow(() -> new RuntimeException("Institution not found"));
        List<Program> programs = programRepository.findAllByInstitutionID(institutionID).orElseThrow(() -> new RuntimeException("Programs not found"));
        programs.forEach(program ->
                institution.getProgramsID().remove(program.getId())
        );
        programs.forEach(program -> moduleService.deleteAllProgramModules(program.getId()));
        programRepository.deleteAll(programs);
    }

    @Override
    public void addProgramToInstitution(String programID, String institutionID) {
        Institution institution = institutionRepository.findById(institutionID).orElseThrow(() -> new RuntimeException("Institution not found"));
        if(institution.getProgramsID()==null) {
            institution.setProgramsID(new ArrayList<>());
        }
        if (!institution.getProgramsID().contains(programID)) {
                institution.getProgramsID().add(programID);
                institutionRepository.save(institution);
        }
    }

    @Override
    public void removeProgramFromInstitution(String programID, String institutionID) {
        Institution institution = institutionRepository.findById(institutionID).orElseThrow(() -> new RuntimeException("Institution not found"));
        if(institution.getProgramsID()==null) {
            institution.setProgramsID(new ArrayList<>());
        }
        if (institution.getProgramsID().contains(programID)) {
            institution.getProgramsID().remove(programID);
            institutionRepository.save(institution);
        }
    }
}
