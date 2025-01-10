package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;

import java.math.BigDecimal;

public record DueStatusReport(
        String regdNo,
        Integer regdYear,
        String name,
        String course,
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
    public DueStatusReport(
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
        // Delegate to the canonical constructor
        this(
                regdNo,
                regdYear,
                name,
                course.getDisplayName(),
                branch,
                arrearsDue,
                currentDues,
                totalDues,
                arrearsPaid,
                currentDuesPaid,
                totalPaid,
                amountDue,
                phNo,
                parentContact
        );
    }
}
