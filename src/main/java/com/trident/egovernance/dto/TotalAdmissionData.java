package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;

public record TotalAdmissionData(
        String admissionYear,
        Courses course,
        String branch,
        StudentType studentType,
        long totalAdmissions
) {
}
