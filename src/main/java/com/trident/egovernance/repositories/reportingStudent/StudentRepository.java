package com.trident.egovernance.repositories.reportingStudent;

import com.trident.egovernance.entities.reportingStudent.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
}
