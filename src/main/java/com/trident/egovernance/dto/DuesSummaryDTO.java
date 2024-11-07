package com.trident.egovernance.dto;

import java.math.BigDecimal;

public record DuesSummaryDTO(
        BigDecimal totalAmountDue,
        BigDecimal totalAmountPaid,
        BigDecimal totalBalanceAmount
) {
}
