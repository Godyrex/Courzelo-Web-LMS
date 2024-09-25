package org.example.courzelo.serviceImpls;

import lombok.AllArgsConstructor;
import org.example.courzelo.dto.requests.module.ModuleRequest;
import org.example.courzelo.dto.responses.module.ModuleResponse;
import org.example.courzelo.dto.responses.module.PaginatedModulesResponse;
import org.example.courzelo.models.institution.Module;
import org.example.courzelo.models.institution.Program;
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
public class ModuleServiceImpl implements IModuleService {
    private final ModuleRepository moduleRepository;
    private final ProgramRepository programRepository;
    @Override
    public ResponseEntity<HttpStatus> createModule(ModuleRequest moduleRequest) {
        if(moduleRequest.getName() == null || moduleRequest.getDescription() == null || moduleRequest.getProgram() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(moduleRepository.existsByNameAndProgram(moduleRequest.getName(), moduleRequest.getProgram())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Program program = programRepository.findById(moduleRequest.getProgram()).orElseThrow(() -> new RuntimeException("Program not found"));
        Module module = Module.builder()
                .name(moduleRequest.getName())
                .description(moduleRequest.getDescription())
                .program(moduleRequest.getProgram())
                .credit(moduleRequest.getCredit())
                .institutionID(program.getInstitutionID())
                .build();
        moduleRepository.save(module);
        addModuleToProgram(program, module.getId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<HttpStatus> updateModule(String id, ModuleRequest moduleRequest) {
        Module module = moduleRepository.findById(id).orElseThrow(() -> new RuntimeException("Module not found"));
        module.setName(moduleRequest.getName());
        module.setDescription(moduleRequest.getDescription());
        module.setCredit(moduleRequest.getCredit());
        if(!moduleRequest.getProgram().equals(module.getProgram())) {
            module.setProgram(moduleRequest.getProgram());
            Program program = programRepository.findById(moduleRequest.getProgram()).orElseThrow(() -> new RuntimeException("Program not found"));
            addModuleToProgram(program, module.getId());
            Program oldProgram = programRepository.findById(module.getProgram()).orElseThrow(() -> new RuntimeException("Program not found"));
            removeModuleFromProgram(oldProgram, module.getId());
        }
        moduleRepository.save(module);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<HttpStatus> deleteModule(String id) {
        Module module = moduleRepository.findById(id).orElseThrow(() -> new RuntimeException("Module not found"));
        Program program = programRepository.findById(module.getProgram()).orElseThrow(() -> new RuntimeException("Program not found"));
        removeModuleFromProgram(program, id);
        moduleRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PaginatedModulesResponse> getModulesByProgram(int page, int size, String programID, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Module> modulePage;
        if(keyword == null) {
            modulePage = moduleRepository.findAllByProgram(programID, pageable);
        } else {
            modulePage = moduleRepository.findAllByProgramAndNameContainingIgnoreCase(programID, keyword, pageable);
        }
        return new ResponseEntity<>(PaginatedModulesResponse.builder()
                .modules(modulePage.getContent().stream().map(
                        module -> ModuleResponse.builder()
                                .id(module.getId())
                                .name(module.getName())
                                .description(module.getDescription())
                                .credit(module.getCredit())
                                .program(module.getProgram())
                                .institutionID(module.getInstitutionID())
                                .build()
                ).toList())
                .totalPages(modulePage.getTotalPages())
                .totalItems(modulePage.getTotalElements())
                .currentPage(modulePage.getNumber())
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ModuleResponse> getModuleById(String id) {
        Module module = moduleRepository.findById(id).orElseThrow(() -> new RuntimeException("Module not found"));
        return new ResponseEntity<>(ModuleResponse.builder()
                .id(module.getId())
                .name(module.getName())
                .description(module.getDescription())
                .credit(module.getCredit())
                .program(module.getProgram())
                .institutionID(module.getInstitutionID())
                .build(), HttpStatus.OK);
    }

    @Override
    public void deleteAllProgramModules(String programID) {
        Program program = programRepository.findById(programID).orElseThrow(() -> new RuntimeException("Program not found"));
        if (program.getModules() != null) {
            program.getModules().forEach(moduleID -> {
                Module module = moduleRepository.findById(moduleID).orElseThrow(() -> new RuntimeException("Module not found"));
                moduleRepository.delete(module);
            });
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
        program.getModules().remove(moduleID);
        programRepository.save(program);
    }
}
