package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.Fees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeesRepository extends JpaRepository<Fees,Long> {
    List<Fees> findAllByBatchId(String batchId);
}
