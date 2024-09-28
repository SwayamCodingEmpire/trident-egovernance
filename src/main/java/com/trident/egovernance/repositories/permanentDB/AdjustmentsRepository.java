package com.trident.egovernance.repositories.permanentDB;

import com.trident.egovernance.entities.permanentDB.Adjustments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdjustmentsRepository extends JpaRepository<Adjustments,Long> {
}
