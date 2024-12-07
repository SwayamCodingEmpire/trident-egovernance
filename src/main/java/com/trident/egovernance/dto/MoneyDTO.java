package com.trident.egovernance.dto;

import java.math.BigDecimal;

public record MoneyDTO(
        BigDecimal amount,
        String amountInWords
) {
}
