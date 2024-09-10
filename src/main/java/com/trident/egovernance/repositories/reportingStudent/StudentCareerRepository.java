package com.trident.egovernance.repositories.reportingStudent;

import com.trident.egovernance.entities.reportingStudent.StudentCareer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentCareerRepository extends JpaRepository<StudentCareer, String> {
}
