package com.trident.egovernance.dto;

import java.util.Set;

public record FeeCollectionAndMrDetails(
        FeeCollectionOnlyDTO feeCollection,
        Set<MrDetailsDTOMinimal> mrDetails
) {
}
