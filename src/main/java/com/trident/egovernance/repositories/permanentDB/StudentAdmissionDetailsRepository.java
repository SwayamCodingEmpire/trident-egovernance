package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.StudentAdmissionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentAdmissionDetailsRepository extends JpaRepository<StudentAdmissionDetails,String> {
}
