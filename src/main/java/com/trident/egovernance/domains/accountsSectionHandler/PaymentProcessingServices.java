package com.trident.egovernance.domains.accountsSectionHandler;

import com.trident.egovernance.dto.MrDetailsSorted;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;

public interface PaymentProcessingServices {
    MrDetailsSorted processPayment(FeeCollection feeCollection, String regdNo);
}
