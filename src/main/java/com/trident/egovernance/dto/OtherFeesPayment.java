package com.trident.egovernance.dto;

import java.util.Set;

public record OtherFeesPayment(
        Long mrNo,
        OtherFeeCollection feeCollection,
        Set<MrDetailsDTOMinimal> otherMrDetails
) {
}
