package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.dto.DuesDetailsDto;
import com.trident.egovernance.global.entities.permanentDB.OldDueDetails;
import com.trident.egovernance.global.helpers.DuesDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OldDuesDetailsRepository extends JpaRepository<OldDueDetails, DuesDetailsId> {
    List<DuesDetailsDto> findAllByRegdNo(String regdNo);
}
