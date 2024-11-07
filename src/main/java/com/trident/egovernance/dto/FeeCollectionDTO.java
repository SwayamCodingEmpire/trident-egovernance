package com.trident.egovernance.dto;

public record FeeCollectionDTO(
        Long mrNo,
        int dueYear,
        String sessionId,
        String regdNo
) {
}
