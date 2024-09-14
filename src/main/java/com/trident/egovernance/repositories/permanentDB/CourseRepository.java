package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course,String> {
}
