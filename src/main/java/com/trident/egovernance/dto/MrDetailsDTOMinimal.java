package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.MrDetails;

import java.math.BigDecimal;

public record MrDetailsDTOMinimal(
        long slNo,
        String particulars,
        BigDecimal amount
) {

    public MrDetailsDTOMinimal(MrDetails mrDetails) {
        this(
                mrDetails.getSlNo(),
                mrDetails.getParticulars(),
                mrDetails.getAmount()
        );
    }
}
