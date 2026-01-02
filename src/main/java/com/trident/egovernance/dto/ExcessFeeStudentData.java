package com.trident.egovernance.dto;


import com.trident.egovernance.global.helpers.CollegeName;

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
        BigDecimal excessFeePaid,
        CollegeName collegeName
) {
}
