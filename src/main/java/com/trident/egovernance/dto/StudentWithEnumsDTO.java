package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.BooleanString;
import com.trident.egovernance.global.helpers.Gender;
import com.trident.egovernance.global.helpers.StudentStatus;
import com.trident.egovernance.global.helpers.StudentType;

public record StudentWithEnumsDTO(
        String regdNo,
        String studentName,
        Gender gender,
        StudentType studentType,
        BooleanString hostelier,
        BooleanString transportAvailed,
        StudentStatus status
) {}
