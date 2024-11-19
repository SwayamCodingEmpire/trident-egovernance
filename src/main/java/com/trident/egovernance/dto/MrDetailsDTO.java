package com.trident.egovernance.dto;

import java.math.BigDecimal;

public record MrDetailsDTO(
        long slNo,
        String particulars,
        BigDecimal amount
) {
}
