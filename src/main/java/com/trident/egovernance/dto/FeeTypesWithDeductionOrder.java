package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.FeeTypes;
import com.trident.egovernance.global.helpers.FeeTypesType;
import com.trident.egovernance.global.helpers.MrHead;

public record FeeTypesWithDeductionOrder(
        String description,
        FeeTypesType type,
        String feeGroup,
        MrHead mrHead,
        String partOf,
        Integer semester,
        Integer deductionOrder
) {
    public FeeTypesWithDeductionOrder(FeeTypes feeTypes){
        this(
                feeTypes.getDescription(),
                feeTypes.getType(),
                feeTypes.getFeeGroup(),
                feeTypes.getMrHead(),
                feeTypes.getPartOf(),
                feeTypes.getSemester() == null ? -1 : feeTypes.getSemester(),
                feeTypes.getStandardDeductionFormat() == null ? -1 : feeTypes.getStandardDeductionFormat().getDeductionOrder()
        );
    }
}
