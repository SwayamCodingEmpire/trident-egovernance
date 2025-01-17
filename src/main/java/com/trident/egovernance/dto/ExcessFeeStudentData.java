package com.trident.egovernance.dto;


import java.math.BigDecimal;

public record ExcessFeeStudentData(
        String regdNo,
        String name,
        String branchCode,
        String admissionYear,
        Integer regdyear,
        String sessionId,
        BigDecimal grandTotalDues,
        BigDecimal feeCollected,
        BigDecimal jeeFeePaid,
        BigDecimal excessFeePaid
) {
}
