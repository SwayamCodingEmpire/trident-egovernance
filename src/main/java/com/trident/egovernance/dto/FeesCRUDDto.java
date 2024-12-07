package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.Fees;

import java.util.Set;

public record FeesCRUDDto(
        BasicFeeBatchDetails batchElements,
        Set<Fees> feesList
) {
}
