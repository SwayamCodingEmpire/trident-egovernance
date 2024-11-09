package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.global.entities.permanentDB.MrDetails;
import com.trident.egovernance.global.entities.views.DailyCollectionSummary;
import com.trident.egovernance.global.helpers.PaymentMode;

import java.math.BigDecimal;

public record CollectionSummary(
        String paymentDate,
        PaymentMode paymentMode,
        String particulars,
        BigDecimal totalAmount,
        int sem
) {
    public CollectionSummary(MrDetails mrDetails) {
        this(
                mrDetails.getFeeCollection().getPaymentDate(),
                mrDetails.getFeeCollection().getPaymentMode(),
                mrDetails.getParticulars(),
                mrDetails.getAmount(),
                mrDetails.getFeeType().getSemester() == null ? -1 : mrDetails.getFeeType().getSemester()
        );
    }

    public CollectionSummary(DailyCollectionSummary dailyCollectionSummary){
        this(
                dailyCollectionSummary.getPaymentDate(),
                dailyCollectionSummary.getPaymentMode(),
                dailyCollectionSummary.getParticulars(),
                dailyCollectionSummary.getTotalAmount(),
                dailyCollectionSummary.getSem()
        );
    }
}
