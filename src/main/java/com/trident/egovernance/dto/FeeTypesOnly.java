package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.FeeTypes;
import com.trident.egovernance.global.helpers.FeeTypesType;
import com.trident.egovernance.global.helpers.MrHead;

public record FeeTypesOnly(
        String description,
        FeeTypesType type,
        String feeGroup,
        MrHead mrHead,
        String partOf,
        Integer semester
) {
    public FeeTypesOnly(FeeTypes feeTypes){
        this(
                feeTypes.getDescription(),
                feeTypes.getType(),
                feeTypes.getFeeGroup(),
                feeTypes.getMrHead(),
                feeTypes.getPartOf(),
                feeTypes.getSemester()
        );
    }
}
