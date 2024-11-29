package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.FeeTypesType;

public record DescriptionTypeSemester(
        String description,
        FeeTypesType type,
        Integer semester
) {
}
