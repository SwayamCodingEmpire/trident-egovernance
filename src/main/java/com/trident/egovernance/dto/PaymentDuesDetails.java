package com.trident.egovernance.dto;

import java.math.BigDecimal;

public record PaymentDuesDetails(
        BigDecimal arrears,
        BigDecimal currentDues,
        BigDecimal totalPaid,
        BigDecimal amountDue
) {

    public PaymentDuesDetails(BigDecimal currentDues, BigDecimal totalPaid, BigDecimal amountDue) {
        this(
                BigDecimal.ZERO,
                currentDues,
                totalPaid,
                amountDue
        );
    }

    public PaymentDuesDetails(com.trident.egovernance.global.entities.permanentDB.PaymentDuesDetails paymentDuesDetails) {
        this(
                paymentDuesDetails.getArrears(),
                paymentDuesDetails.getCurrentDues(),
                paymentDuesDetails.getTotalPaid(),
                paymentDuesDetails.getAmountDue()
        );
    }
}
