package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;

public record SessionInitiationDto(
        Integer admYear,
        Courses courses,
        Integer regdYear,
        StudentType studentType

) {
}
