package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;

public record CourseBranchStudentCountDTO(
        Courses course,
        String branchCode,
        long studentCount
) {
}
