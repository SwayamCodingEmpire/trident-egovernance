package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.global.helpers.PaymentMode;

public record FeeCollectionDetails(
        String date,
        PaymentMode paymentMode,
        String ddNo,
        String ddDate,
        String ddBank
) {
    public FeeCollectionDetails(FeeCollection feeCollection) {
        this(
                feeCollection.getPaymentDate(),
                feeCollection.getPaymentMode(),
                feeCollection.getDdNo(),
                feeCollection.getDdDate(),
                feeCollection.getDdBank()
        );
    }
}
