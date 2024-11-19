package com.trident.egovernance.dto;

import java.math.BigDecimal;

public record OtherMrDetails(
        long slNo,
        String particulars,
        BigDecimal amount
) {
}
