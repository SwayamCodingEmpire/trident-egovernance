package com.trident.egovernance.global.entities.permanentDB;

import java.math.BigDecimal;

public record PaymentDuesDetails(
        BigDecimal arrears,
        BigDecimal currentDues,
        BigDecimal totalPaid,
        BigDecimal amountDue
) {
}
