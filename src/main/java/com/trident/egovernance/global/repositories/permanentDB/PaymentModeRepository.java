package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.PaymentMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
@Repository
public interface PaymentModeRepository extends JpaRepository<PaymentMode,String> {
    @Query("SELECT DISTINCT(p.pmo) FROM PAYMENTMODE p")
    Set<String> findAllPaymentModes();
}
