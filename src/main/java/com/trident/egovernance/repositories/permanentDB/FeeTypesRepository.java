package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.FeeTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeTypesRepository extends JpaRepository<FeeTypes,String> {
    List<FeeTypes> findByDescriptionIn(List<String> descriptions);
}
