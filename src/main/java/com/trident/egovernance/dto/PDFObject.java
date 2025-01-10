package com.trident.egovernance.dto;

public record PDFObject(
        StudentBasicDTO personalDetails,
        MoneyReceipt mrDetails,
        String url,
        String paymentReceiver
) {
}
