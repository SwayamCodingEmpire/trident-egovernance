package com.trident.egovernance.dto;

import java.sql.Date;

public record CollectionReportInputDTO(
        Date fromDate,
        Date toDate,
        String session
) {
    public CollectionReportInputDTO {
        // Validate session format
        if (session != null && !session.matches("\\d{4}-\\d{4}")) {
            throw new IllegalArgumentException("Invalid format for session. Expected format: yyyy-yyyy or null");
        }

        // Ensure fromDate and toDate are either both null or both non-null
        if ((fromDate == null && toDate != null) || (fromDate != null && toDate == null)) {
            throw new IllegalArgumentException("Both fromDate and toDate must be null together or non-null together.");
        }

        // Ensure fromDate and toDate are non-null if session is null, and vice versa
        if ((fromDate != null || toDate != null) && session != null) {
            throw new IllegalArgumentException("If fromDate and toDate are non-null, session must be null.");
        }

        if ((fromDate == null && toDate == null) && session == null) {
            throw new IllegalArgumentException("If session is null, both fromDate and toDate must be non-null.");
        }
    }
}
