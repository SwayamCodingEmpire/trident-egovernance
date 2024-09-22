package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.Fees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeesRepository extends JpaRepository<Fees,Long> {
    @Query("SELECT f FROM FEES f JOIN FETCH f.feeType WHERE f.batchId = :batchId")
    List<Fees> findAllByBatchId(@Param("batchId") String batchId);
    @Query("SELECT f FROM FEES f JOIN FETCH f.feeType WHERE f.description IN :descriptions")
    List<Fees> findByDescriptionIn(@Param("descriptions") List<String> descriptions);

}
