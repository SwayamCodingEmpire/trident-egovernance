package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.FeeCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeCollectionRepository extends JpaRepository<FeeCollection,Long> {
    @Query("select MAX(f.mrNo) from FEECOLLECTION f")
    Long getMaxMrNo();
}
