package com.trident.egovernance.global.repositories.permanentDB;

import com.trident.egovernance.global.entities.permanentDB.PaymentDuesDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDuesDetailsRepository extends JpaRepository<PaymentDuesDetails, Long> {
}
