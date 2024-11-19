package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.PaymentMode;

public record FeeCollectionDetails(
        PaymentMode paymentMode,
        String ddNo,
        String ddDate,
        String ddBank
) {
}
