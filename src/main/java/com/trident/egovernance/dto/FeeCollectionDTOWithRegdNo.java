package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.FeeCollection;

public record FeeCollectionDTOWithRegdNo(
        FeeCollection feeCollection,
        String regdNo
) {

    public FeeCollectionDTOWithRegdNo(FeeCollection feeCollection, String regdNo) {
        this.feeCollection = feeCollection;
        this.regdNo = regdNo;
    }
}
