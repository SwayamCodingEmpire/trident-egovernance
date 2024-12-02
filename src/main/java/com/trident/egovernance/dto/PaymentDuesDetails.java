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
}
