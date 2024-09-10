package com.trident.egovernance.repositories.reportingStudent;

import com.trident.egovernance.entities.reportingStudent.PersonalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalDetailsRepository extends JpaRepository<PersonalDetails, String> {
}
