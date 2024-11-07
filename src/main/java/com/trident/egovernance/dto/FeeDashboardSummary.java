package com.trident.egovernance.dto;

import java.math.BigDecimal;

public record FeeDashboardSummary(
        BigDecimal amountCollectedToday,
        DuesSummaryDTO duesSummary
) {
}
