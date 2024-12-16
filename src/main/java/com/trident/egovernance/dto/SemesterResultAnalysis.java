package com.trident.egovernance.dto;

import java.math.BigDecimal;

public record SemesterResultAnalysis(
        BigDecimal maxGPA,
        BigDecimal avgSGPA,
        BigDecimal studentGPA
) {
}
