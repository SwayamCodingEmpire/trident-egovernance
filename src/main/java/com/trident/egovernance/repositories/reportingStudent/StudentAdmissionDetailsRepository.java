package com.trident.egovernance.repositories.reportingStudent;

import com.trident.egovernance.entities.reportingStudent.StudentAdmissionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentAdmissionDetailsRepository extends JpaRepository<StudentAdmissionDetails,String> {
}
