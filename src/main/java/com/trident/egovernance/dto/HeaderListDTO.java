package com.trident.egovernance.dto;

import java.util.List;
import java.util.Set;

public record HeaderListDTO(
        Set<String> sessions,
        Set<String> particulars,
        Set<String> paymentModes
) {
}
