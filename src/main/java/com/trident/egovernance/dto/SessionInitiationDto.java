package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;

public record SessionInitiationDto(
        String admYear,
        Courses courses,
        Integer regdYear,
        StudentType studentType

) {
}
