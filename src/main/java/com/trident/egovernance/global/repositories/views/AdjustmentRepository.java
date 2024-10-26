package com.trident.egovernance.global.repositories.views;

import com.trident.egovernance.global.entities.views.Adjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface AdjustmentRepository extends JpaRepository<Adjustment,String> {
}
