package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeCollectionRepository extends JpaRepository<FeeCollection,Long> {
    @Query("select MAX(f.mrNo) from FEECOLLECTION f")
    Long getMaxMrNo();

    List<FeeCollection> findAllByStudent_RegdNo(String regdNo);
}
