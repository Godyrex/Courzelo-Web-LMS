package org.example.courzelo.repositories;

import org.example.courzelo.models.institution.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends MongoRepository<Grade, String> {
    Optional<List<Grade>> findAllByGroupID(String groupID);
    Optional<List<Grade>> findAllByGroupIDAndModuleID(String groupID, String moduleID);
    Optional<Grade> findByNameAndModuleIDAndStudentEmail(String name, String moduleID, String studentEmail);
    Optional<List<Grade>> findByModuleIDAndStudentEmail( String moduleID, String studentEmail);
    Optional<List<Grade>> findByStudentEmailAndGroupID(String email,String groupID);
}
