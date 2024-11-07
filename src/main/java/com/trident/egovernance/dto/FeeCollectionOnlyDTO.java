package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.global.helpers.PaymentMode;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;

public record FeeCollectionOnlyDTO(
        Long mrNo,
        BigDecimal collectedFee,
        PaymentMode paymentMode,
        String ddNo,
        String ddDate,
        String ddBank,
        String paymentDate,
        int dueYear,
        String sessionId,
        String regdNo
) {
    public FeeCollectionOnlyDTO(FeeCollection feeCollection) {
        this(
                feeCollection.getMrNo(),
                feeCollection.getCollectedFee(),
                feeCollection.getPaymentMode(),
                feeCollection.getDdNo(),
                feeCollection.getDdDate(),
                feeCollection.getDdBank(),
                feeCollection.getPaymentDate(),
                feeCollection.getDueYear(),
                feeCollection.getSessionId(),
                feeCollection.getStudent().getRegdNo()
        );
    }
}
