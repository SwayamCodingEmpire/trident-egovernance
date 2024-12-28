package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.BooleanString;
import com.trident.egovernance.global.helpers.Courses;

public record StudentCollegeInformation(
        int rollNo,
        int year,
        int semester,
        int labGroup,
        String section,
        Courses course,
        BooleanString hostelAvailed,
        BooleanString transportAvailed
) {
}
