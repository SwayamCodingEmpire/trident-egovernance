package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.entities.permanentDB.FeeCollection;
import com.trident.egovernance.global.entities.permanentDB.MrDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record PaymentProcessingInternalData(
        Set<MrDetails> mrDetails,
        FeeCollection feeCollection,
        BigDecimal collectedFees,
        List<DuesDetails> duesDetails,
        long slNo
) {
}
