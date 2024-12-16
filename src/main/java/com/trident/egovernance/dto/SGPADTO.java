package com.trident.egovernance.dto;

import java.math.BigDecimal;

public record SGPADTO(
        BigDecimal sgpa1,
        BigDecimal sgpa2,
        BigDecimal sgpa3,
        BigDecimal sgpa4,
        BigDecimal sgpa5,
        BigDecimal sgpa6,
        BigDecimal sgpa7,
        BigDecimal sgpa8,
        BigDecimal cgpa
) {
    public SGPADTO(BigDecimal sgpa1, BigDecimal sgpa2, BigDecimal sgpa3, BigDecimal sgpa4,
                   BigDecimal sgpa5, BigDecimal sgpa6, BigDecimal sgpa7, BigDecimal sgpa8, BigDecimal cgpa) {
        this.sgpa1 = (sgpa1 == null) ? BigDecimal.ZERO : sgpa1;
        this.sgpa2 = (sgpa2 == null) ? BigDecimal.ZERO : sgpa2;
        this.sgpa3 = (sgpa3 == null) ? BigDecimal.ZERO : sgpa3;
        this.sgpa4 = (sgpa4 == null) ? BigDecimal.ZERO : sgpa4;
        this.sgpa5 = (sgpa5 == null) ? BigDecimal.ZERO : sgpa5;
        this.sgpa6 = (sgpa6 == null) ? BigDecimal.ZERO : sgpa6;
        this.sgpa7 = (sgpa7 == null) ? BigDecimal.ZERO : sgpa7;
        this.sgpa8 = (sgpa8 == null) ? BigDecimal.ZERO : sgpa8;
        this.cgpa = (cgpa == null) ? BigDecimal.ZERO : cgpa;
    }
}
