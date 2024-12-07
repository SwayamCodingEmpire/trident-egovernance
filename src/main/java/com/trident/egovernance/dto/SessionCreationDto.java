package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

public record SessionCreationDto(
        String sessionId,
        Date startDate,
        Date endDate,
        Courses course,
        Integer regdYear,
        String prevSessionId,
        Integer admissionYear,
        StudentType studentType
) {
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yy");

    public SessionCreationDto(
            String sessionId,
            Date startDate,
            Date endDate,
            Courses course,
            Integer regdYear,
            String prevSessionId,
            Integer admissionYear,
            StudentType studentType
    ) {
        // Validate sessionId format
        if ((!sessionId.matches("\\d{4}-\\d{4}")) || (!prevSessionId.matches("\\d{4}-\\d{4}"))) {
            throw new IllegalArgumentException("sessionId and prevSessionId must be in yyyy-yyyy format.");
        }

        // Validate regdYear is a single-digit integer
        if (regdYear != null && (regdYear < 0 || regdYear > 9)) {
            throw new IllegalArgumentException("regdYear must be a single-digit integer (0-9).");
        }

        // Validate admissionYear range
        if (admissionYear != null && (admissionYear < 2000 || admissionYear > 2100)) {
            throw new IllegalArgumentException("admissionYear must be between 2000 and 2100.");
        }

        // Assign default values to startDate and endDate if null
        this.startDate = Objects.requireNonNullElseGet(startDate, SessionCreationDto::getCurrentDate);
        this.endDate = Objects.requireNonNullElseGet(endDate, SessionCreationDto::getCurrentDate);
        this.sessionId = sessionId;
        this.course = course;
        this.regdYear = regdYear;
        this.prevSessionId = prevSessionId;
        this.admissionYear = admissionYear;
        this.studentType = studentType;
    }

    private static Date getCurrentDate() {
        try {
            // Format the current date to the desired format
            String formattedDate = DATE_FORMATTER.format(new java.util.Date());
            // Parse it back to a java.util.Date
            java.util.Date utilDate = DATE_FORMATTER.parse(formattedDate);
            // Convert to java.sql.Date
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            throw new RuntimeException("Error parsing current date.", e);
        }
    }

    public String getFormattedStartDate() {
        return DATE_FORMATTER.format(startDate);
    }

    public String getFormattedEndDate() {
        return DATE_FORMATTER.format(endDate);
    }
}
