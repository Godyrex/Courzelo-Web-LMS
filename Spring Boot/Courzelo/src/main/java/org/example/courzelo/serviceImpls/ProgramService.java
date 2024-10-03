package org.example.courzelo.serviceImpls;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.courzelo.dto.requests.program.ProgramRequest;
import org.example.courzelo.dto.responses.program.PaginatedProgramsResponse;
import org.example.courzelo.dto.responses.program.ProgramResponse;
import org.example.courzelo.dto.responses.program.SimplifiedProgramResponse;
import org.example.courzelo.exceptions.*;
import org.example.courzelo.models.User;
import org.example.courzelo.models.institution.Group;
import org.example.courzelo.models.institution.Institution;
import org.example.courzelo.models.institution.Program;
import org.example.courzelo.repositories.GroupRepository;
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
@Slf4j
public class ProgramService implements IProgramService {
    private final ProgramRepository programRepository;
    private final UserRepository userRepository;
    private final IModuleService moduleService;
    private final InstitutionRepository institutionRepository;
    private final GroupServiceImpl groupService;
    private final GroupRepository groupRepository;
    @Override
    public ResponseEntity<HttpStatus> createProgram(ProgramRequest programRequest, Principal principal) {
        if(programRequest.getName() == null || programRequest.getDescription() == null) {
            throw new RequestNotValidException("Name and description are required");
        }
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new UserNotFoundException('"' + principal.getName() + '"' + " not found"));
        if(user.getEducation() == null) {
            throw new NotAllowedException("User is not part of an institution");
        }
        if(programRepository.existsByNameAndInstitutionID(programRequest.getName(), user.getEducation().getInstitutionID())) {
            throw new ProgramAlreadyExistsException("Program with name " + programRequest.getName() + " already exists");
        }
        Program program = Program.builder()
                .name(programRequest.getName())
                .description(programRequest.getDescription())
                .institutionID(user.getEducation().getInstitutionID())
                .credits(programRequest.getCredits())
                .duration(programRequest.getDuration())
                .groups(new ArrayList<>())
                .modules(new ArrayList<>())
                .build();
        programRepository.save(program);
        addProgramToInstitution(program.getId(), user.getEducation().getInstitutionID());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<HttpStatus> updateProgram(String id, ProgramRequest programRequest) {
        if(programRequest.getName() == null || programRequest.getDescription() == null) {
            throw new RequestNotValidException("Name and description are required");
        }
        Program program = programRepository.findById(id).orElseThrow(() -> new ProgramNotFoundException("Program not found"));
        if(!program.getName().equals(programRequest.getName()) && programRepository.existsByNameAndInstitutionID(programRequest.getName(), program.getInstitutionID())) {
            throw new ProgramAlreadyExistsException("Program with name " + programRequest.getName() + " already exists");
        }
        program.setName(programRequest.getName());
        program.setDescription(programRequest.getDescription());
        program.setCredits(programRequest.getCredits());
        program.setDuration(programRequest.getDuration());
        programRepository.save(program);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteProgram(String id) {
        Program program = programRepository.findById(id).orElseThrow(() -> new ProgramNotFoundException("Program not found"));
        removeProgramFromInstitution(program.getId(), program.getInstitutionID());
        moduleService.deleteAllProgramModules(program.getId());
        if(program.getGroups()!=null) {
            program.getGroups().forEach(groupService::deleteGroup);
        }
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
                                .credits(program.getCredits())
                                .duration(program.getDuration())
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
        Program program = programRepository.findById(id).orElseThrow(() -> new ProgramNotFoundException("Program not found"));
        ProgramResponse programResponse = ProgramResponse.builder()
                .id(program.getId())
                .name(program.getName())
                .description(program.getDescription())
                .institutionID(program.getInstitutionID())
                .credits(program.getCredits())
                .duration(program.getDuration())
                .build();
        return new ResponseEntity<>(programResponse, HttpStatus.OK);
    }

    @Override
    public void deleteAllInstitutionPrograms(String institutionID) {
        Institution institution = institutionRepository.findById(institutionID).orElseThrow(() -> new InstitutionNotFoundException("Institution not found"));
        if (institution.getProgramsID() != null) {
            institution.getProgramsID().forEach(this::deleteProgram);
            institution.setProgramsID(new ArrayList<>());
            institutionRepository.save(institution);
        }
    }

    @Override
    public void addProgramToInstitution(String programID, String institutionID) {
        Institution institution = institutionRepository.findById(institutionID).orElseThrow(() -> new InstitutionNotFoundException("Institution not found"));
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
        Institution institution = institutionRepository.findById(institutionID).orElseThrow(() -> new InstitutionNotFoundException("Institution not found"));
        if (institution.getProgramsID()!= null&&institution.getProgramsID().contains(programID)) {
            institution.getProgramsID().remove(programID);
            institutionRepository.save(institution);
        }
    }

    @Override
    public ResponseEntity<SimplifiedProgramResponse> getSimplifiedProgramById(String id) {
        Program program = programRepository.findById(id).orElseThrow(() -> new ProgramNotFoundException("Program not found"));
        SimplifiedProgramResponse simplifiedProgramResponse = SimplifiedProgramResponse.builder()
                .id(program.getId())
                .name(program.getName())
                .build();
        return new ResponseEntity<>(simplifiedProgramResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<SimplifiedProgramResponse>> getSimplifiedProgramsByInstitution(String institutionID) {
        List<Program> programs = programRepository.findAllByInstitutionID(institutionID).orElseThrow(() -> new ProgramNotFoundException("Programs not found"));
        List<SimplifiedProgramResponse> simplifiedProgramResponses = programs.stream().map(
                program -> SimplifiedProgramResponse.builder()
                        .id(program.getId())
                        .name(program.getName())
                        .build()
        ).toList();
        return new ResponseEntity<>(simplifiedProgramResponses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ProgramResponse> getMyProgram(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow(() -> new UserNotFoundException('"' + principal.getName() + '"' + " not found"));
        if(user.getEducation() == null) {
            throw new NotAllowedException("User is not part of an institution");
        }
        if(user.getEducation().getGroupID()== null)
        {
            throw new NotAllowedException("User is not part of a group");
        }
        Group group = groupRepository.findById(user.getEducation().getGroupID()).orElseThrow(() -> new GroupNotFoundException("Group not found"));
        log.info("Group: " + group);
        Program program = programRepository.findById(group.getProgram()).orElseThrow(() -> new ProgramNotFoundException("Program not found"));
        log.info("Program: " + program);
        ProgramResponse programResponse = ProgramResponse.builder()
                .id(program.getId())
                .name(program.getName())
                .description(program.getDescription())
                .institutionID(program.getInstitutionID())
                .credits(program.getCredits())
                .duration(program.getDuration())
                .build();
        return new ResponseEntity<>(programResponse, HttpStatus.OK);
    }
}
