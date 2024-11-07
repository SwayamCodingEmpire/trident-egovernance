package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.BooleanString;
import com.trident.egovernance.global.helpers.TFWType;

public record StudentRequiredFieldsDTO(
        TFWType tfw,
        BooleanString transportOpted,
        BooleanString hostelOption
) {
}
