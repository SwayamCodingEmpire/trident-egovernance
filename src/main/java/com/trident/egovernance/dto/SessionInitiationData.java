package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;

import java.sql.Date;
import java.util.Set;

public record SessionInitiationData(
        Integer admYear,
        String prevSessionId,
        Date startDate,
        String sessionId,
        Courses course,
        Set<String> regdNos,
        StudentType studentType,
        int currentYear,
        boolean promotionType,
        Set<String> notPromoted
) {
    public SessionInitiationData {
        // Check if prevSessionId is provided, otherwise set sessionId to a generated value
        if (prevSessionId == null || prevSessionId.isEmpty()) {
            throw new IllegalArgumentException("prevSessionId cannot be null or empty");
        }

        // Generate sessionId based on prevSessionId and other properties (you can change the logic)
        sessionId = generateSessionId(prevSessionId);
    }

    // Custom method to generate sessionId from prevSessionId
    private String generateSessionId(String prevSessionId) {
        // Extract the starting and ending years from the prevSessionId (format: YYYY-YYYY)
        String[] parts = prevSessionId.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid prevSessionId format. Expected 'YYYY-YYYY'");
        }

        // Parse the years and increment the starting year by 1
        try {
            int startYear = Integer.parseInt(parts[0]);
            int endYear = Integer.parseInt(parts[1]);

            // Generate the new sessionId as 'startYear+1-endYear+1'
            return (startYear + 1) + "-" + (endYear + 1);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid year format in prevSessionId");
        }
    }
}
