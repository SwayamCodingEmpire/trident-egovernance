package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.ExcessRefund;
import com.trident.egovernance.global.helpers.ExcessRefundID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcessRefundRepository extends JpaRepository<ExcessRefund, ExcessRefundID> {
}
