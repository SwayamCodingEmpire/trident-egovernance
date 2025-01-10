package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.PaymentMode;

import java.math.BigDecimal;

public record ExcessRefundDTO(
        String voucherNo,
        String regdNo,
        BigDecimal refundAmount,
        PaymentMode refundMode,
        String chqNo,
        String chqDate
        )
{
}
