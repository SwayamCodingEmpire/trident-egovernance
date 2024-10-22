package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;

import java.util.List;

public record CourseStudentCountDTO(
        Courses course,
        long studentCount,
        List<BranchCountDTO> branches
) {
}
