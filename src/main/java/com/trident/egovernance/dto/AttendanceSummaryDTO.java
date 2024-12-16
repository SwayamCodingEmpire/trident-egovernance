package com.trident.egovernance.dto;

public record AttendanceSummaryDTO(
        String subAbbr,
        Integer totalClasses,
        Integer totalAttended
) {
}
