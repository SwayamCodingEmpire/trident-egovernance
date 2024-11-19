package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.PaymentMode;

import java.math.BigDecimal;

public record OtherFeeCollection(
        BigDecimal collectedFee,
        PaymentMode paymentMode
) {
}
