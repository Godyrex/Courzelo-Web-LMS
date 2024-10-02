package org.example.courzelo.repositories;

import org.example.courzelo.models.institution.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends MongoRepository<Course,String> {
    Optional<List<Course>> findAllByTeacher(String teacher);
    void deleteAllByModule(String module);
}
