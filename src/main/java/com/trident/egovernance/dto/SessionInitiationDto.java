package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;

import java.io.Serializable;
import java.util.Date;

public record SessionInitiationDTO(
        String admYear,
        Courses course,
        Integer regdYear,
        StudentType studentType
) implements Serializable {
}
