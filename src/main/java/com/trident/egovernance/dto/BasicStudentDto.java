package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.Gender;

public record BasicStudentDto(
        String regdNo,
        String studentName,
        Gender gender,
        Courses course,
        String branchCode,
        String admissionYear,
        Integer currentYear) {
}
