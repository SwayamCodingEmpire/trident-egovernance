package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.PersonalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalDetailsRepository extends JpaRepository<PersonalDetails, String> {
}
