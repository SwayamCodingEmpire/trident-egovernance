package com.trident.egovernance.dto;

import java.math.BigDecimal;

public record AttendanceSummaryDTO(
        int sem,
        String subAbbr,
        Integer totalClasses,
        Integer totalAttended,
        BigDecimal attendancePercentage
) {
    public AttendanceSummaryDTO(String sem, String subAbbr, Integer totalClasses, Integer totalAttended) {
        this(Integer.parseInt(sem), subAbbr(subAbbr), totalClasses, totalAttended, BigDecimal.valueOf(((double) totalAttended / totalClasses) * 100));
    }

    private static String subAbbr(String section) {
        // Check if the section contains an abbreviation in brackets
        if (section != null && section.contains("(") && section.contains(")")) {
            // Extract the abbreviation inside the brackets
            return section.substring(section.indexOf("(") + 1, section.indexOf(")"));
        }
        // Return the original section if no abbreviation is found
        return section;
    }
}
