package org.example.courzelo.services;

import org.example.courzelo.dto.requests.module.ModuleRequest;
import org.example.courzelo.dto.responses.module.ModuleResponse;
import org.example.courzelo.dto.responses.module.PaginatedModulesResponse;
import org.example.courzelo.models.institution.Program;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public interface IModuleService {
    ResponseEntity<HttpStatus> createModule(ModuleRequest moduleRequest);
    ResponseEntity<HttpStatus> updateModule(String id, ModuleRequest moduleRequest);
    ResponseEntity<HttpStatus> deleteModule(String id);
    ResponseEntity<PaginatedModulesResponse> getModulesByProgram(int page, int size, String programID, String keyword);
    ResponseEntity<ModuleResponse> getModuleById(String id);
    void deleteAllProgramModules(String programID);
    void addModuleToProgram(Program program, String moduleID);
    void removeModuleFromProgram(Program program, String moduleID);
}

