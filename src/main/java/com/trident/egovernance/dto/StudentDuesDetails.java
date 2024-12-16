package com.trident.egovernance.dto;

import java.util.List;
import java.util.Set;

public record StudentDuesDetails(
        Set<DuesDetailsDto> duesDetails,
        PaymentDuesDetails dueSummary
) {
}
