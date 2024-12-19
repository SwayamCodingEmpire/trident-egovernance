package com.trident.egovernance.global.repositories.views;

import com.trident.egovernance.global.entities.views.RollSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RollSheetRepository extends JpaRepository<RollSheet, String> {
    @Query("SELECT s.labGroup FROM ROLLSHEET s WHERE s.regdNo = :regdNo")
    Optional<Integer> getLabGroupByRegdNo(String regdNo);
}
