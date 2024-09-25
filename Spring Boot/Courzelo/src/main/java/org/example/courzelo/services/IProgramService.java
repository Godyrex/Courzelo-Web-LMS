package org.example.courzelo.services;

import org.example.courzelo.dto.requests.program.ProgramRequest;
import org.example.courzelo.dto.responses.program.PaginatedProgramsResponse;
import org.example.courzelo.dto.responses.program.ProgramResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface IProgramService {
    ResponseEntity<HttpStatus> createProgram(ProgramRequest programRequest, Principal principal);
    ResponseEntity<HttpStatus> updateProgram(String id, ProgramRequest programRequest);
    ResponseEntity<HttpStatus> deleteProgram(String id);
    ResponseEntity<PaginatedProgramsResponse> getProgramsByInstitution(int page, int size,String institutionID, String keyword);
    ResponseEntity<ProgramResponse> getProgramById(String id);
    void deleteAllInstitutionPrograms(String institutionID);
    void addProgramToInstitution(String programID, String institutionID);
    void removeProgramFromInstitution(String programID, String institutionID);

}
