package com.trident.egovernance.global.repositories.examDB;

import com.trident.egovernance.global.entities.examDB.StudentExamResults;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentExamResultRepository extends JpaRepository<StudentExamResults, Integer>, ResultCustomDatabase {
}
