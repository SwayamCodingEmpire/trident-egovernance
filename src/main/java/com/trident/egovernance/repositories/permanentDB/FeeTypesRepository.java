package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.FeeTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeTypesRepository extends JpaRepository<FeeTypes,String> {
}
