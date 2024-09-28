package com.trident.egovernance.services;

import com.trident.egovernance.dto.MrDetailsSorted;
import com.trident.egovernance.entities.permanentDB.FeeCollection;

public interface PaymentProcessingServices {
    MrDetailsSorted processPayment(FeeCollection feeCollection, String regdNo);
}
