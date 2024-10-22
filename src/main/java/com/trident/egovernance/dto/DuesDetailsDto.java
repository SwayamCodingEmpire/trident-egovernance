package com.trident.egovernance.dto;

import java.math.BigDecimal;

public record DuesDetailsDto(
        String description,
        BigDecimal amountDue,
        BigDecimal amountPaid,
        BigDecimal amountPaidToJee,
        BigDecimal balanceAmount,
        Integer dueYear
) {
}
