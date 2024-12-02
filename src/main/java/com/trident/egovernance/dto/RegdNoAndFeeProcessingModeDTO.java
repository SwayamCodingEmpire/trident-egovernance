package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.FeeProcessingMode;

public record RegdNoAndFeeProcessingModeDTO(
        String regdNo,
        FeeProcessingMode feeProcessingMode
) {
}
