package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.BaseDuesDetails;
import com.trident.egovernance.global.entities.permanentDB.DuesDetails;
import com.trident.egovernance.global.entities.permanentDB.OldDueDetails;

import java.math.BigDecimal;

public record DuesDetailsDto(
        String description,
        BigDecimal amountDue,
        BigDecimal amountPaid,
        BigDecimal amountPaidToJee,
        BigDecimal balanceAmount,
        Integer dueYear,
        Integer sem
) {
    public DuesDetailsDto(DuesDetails duesDetails) {
        this(
                duesDetails.getDescription(),
                duesDetails.getAmountDue(),
                duesDetails.getAmountPaid(),
                duesDetails.getAmountPaidToJee(),
                duesDetails.getBalanceAmount(),
                duesDetails.getDueYear(),
                duesDetails.getFeeType().getSemester()
        );
    }

    public DuesDetailsDto(OldDueDetails duesDetails) {
        this(
                duesDetails.getDescription(),
                duesDetails.getAmountDue(),
                duesDetails.getAmountPaid(),
                duesDetails.getAmountPaidToJee(),
                duesDetails.getBalanceAmount(),
                duesDetails.getDueYear(),
                duesDetails.getFeeType().getSemester()
        );
    }
}