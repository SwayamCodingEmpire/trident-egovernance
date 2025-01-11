package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.Fees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeesRepository extends JpaRepository<Fees,Long> {
    @Query("SELECT f FROM FEES f JOIN FETCH f.feeType WHERE f.batchId = :batchId AND f.regdYear = :regdYear")
    List<Fees> findAllByBatchIdAndRegdYear(String batchId, Integer regdYear);
    @Query("SELECT f FROM FEES f JOIN FETCH f.feeType WHERE f.description IN :descriptions")
    List<Fees> findByDescriptionIn(@Param("descriptions") List<String> descriptions);
    List<Fees> findAllByDescription(String description);
    @Query("SELECT MAX(f.feeId)+1 FROM FEES f")
    Long getMaxIdForFees();

}
