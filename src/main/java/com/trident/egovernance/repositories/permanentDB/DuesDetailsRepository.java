package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.DuesDetails;
import com.trident.egovernance.helpers.DuesDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DuesDetailsRepository extends JpaRepository<DuesDetails, DuesDetailsId> {
    List<DuesDetails> findAllByRegdNoOrderByDeductionOrder(String regdNo);
}
