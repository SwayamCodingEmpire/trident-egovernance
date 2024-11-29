package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;

public record BasicFeeBatchDetails(
        Integer admYear,
        Courses course,
        String branchCode,
        StudentType studentType
) {
}
