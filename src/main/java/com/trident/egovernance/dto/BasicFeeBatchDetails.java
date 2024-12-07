package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;

public record BasicFeeBatchDetails(
        Integer admYear,
        Courses course,
        String branchCode,
        StudentType studentType
) {
    public BasicFeeBatchDetails {
        // Validate admYear
        if (admYear == null || admYear < 1900 || admYear > 2100) {
            throw new IllegalArgumentException("Admission year must be between 1900 and 2100");
        }

        // Validate course
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }

        // Validate branchCode
        if (branchCode == null || branchCode.isBlank()) {
            throw new IllegalArgumentException("Branch code cannot be null or blank");
        }

        // Validate studentType
        if (studentType == null) {
            throw new IllegalArgumentException("Student type cannot be null");
        }
    }
}
