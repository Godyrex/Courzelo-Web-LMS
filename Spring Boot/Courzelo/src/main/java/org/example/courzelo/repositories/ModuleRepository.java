package org.example.courzelo.repositories;

import org.example.courzelo.models.institution.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends MongoRepository<Module, String> {
    boolean existsByNameAndProgram(String name, String programID);
    Page<Module> findAllByProgram(String programID, Pageable pageable);
    Page<Module> findAllByProgramAndNameContainingIgnoreCase(String programID, String keyword, Pageable pageable);
}
