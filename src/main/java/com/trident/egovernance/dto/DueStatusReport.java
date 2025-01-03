package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;

import java.math.BigDecimal;

public record DueStatusReport(
        String regdNo,
        Integer regdYear,
        String name,
        Courses course,
        String branch,
        BigDecimal arrearsDue,
        BigDecimal currentDues,
        BigDecimal totalDues,
        BigDecimal arrearsPaid,
        BigDecimal currentDuesPaid,
        BigDecimal totalPaid,
        BigDecimal amountDue,
        String phNo,
        String parentContact
) {
}
