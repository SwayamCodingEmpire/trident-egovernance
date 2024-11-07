package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.Adjustments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AdjustmentsRepository extends JpaRepository<Adjustments,Long> {
    List<Adjustments> findAllByRegdNoIn(List<String> regdNos);
    @Modifying
    long deleteAllByRegdNoIn(Set<String> regdNos);
}
