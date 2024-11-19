package com.trident.egovernance.dto;

import java.util.Set;

public record OtherFeesPayment(
        OtherFeeCollection feeCollection,
        Set<OtherMrDetails> otherMrDetails
) {
}
