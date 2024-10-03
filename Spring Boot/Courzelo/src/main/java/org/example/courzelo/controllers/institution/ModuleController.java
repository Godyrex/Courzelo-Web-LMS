package org.example.courzelo.controllers.institution;

import lombok.AllArgsConstructor;
import org.example.courzelo.dto.requests.module.ModuleRequest;
import org.example.courzelo.dto.responses.module.ModuleResponse;
import org.example.courzelo.dto.responses.module.PaginatedModulesResponse;
import org.example.courzelo.services.IModuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/module")
@AllArgsConstructor
@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
public class ModuleController {
    private final IModuleService moduleService;
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN')&&@customAuthorization.isAdminInInstitution()")
    public ResponseEntity<HttpStatus> createModule(@RequestBody ModuleRequest moduleRequest){
        return moduleService.createModule(moduleRequest);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')&&@customAuthorization.isAdminInInstitution()")
    public ResponseEntity<HttpStatus> updateModule(@PathVariable String id, @RequestBody ModuleRequest moduleRequest){
        return moduleService.updateModule(id, moduleRequest);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')&&@customAuthorization.isAdminInInstitution()")
    public ResponseEntity<HttpStatus> deleteModule(@PathVariable String id){
        return moduleService.deleteModule(id);
    }
    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN')&&@customAuthorization.isAdminInInstitution()")
    public ResponseEntity<PaginatedModulesResponse> getModules(@RequestParam int page, @RequestParam int sizePerPage,
                                                               @RequestParam(required = false) String keyword, @RequestParam String programID){
        return moduleService.getModulesByProgram(page, sizePerPage, programID, keyword);
    }
    @GetMapping("/{id}")
    @PreAuthorize("@customAuthorization.canAccessModule(#id)")
    public ResponseEntity<ModuleResponse> getModule(@PathVariable String id){
        return moduleService.getModuleById(id);
    }
}
