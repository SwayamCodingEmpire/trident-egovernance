package com.trident.egovernance.dto;

import java.math.BigDecimal;

public record StudentCareerHistory(
        String course,
        BigDecimal score
) {
}
