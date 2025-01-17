package com.trident.egovernance.dto;

import java.util.List;

public record FeeGroupAndPartOfDTO(
        List<String> feeGroups,
        List<String> partOfs
) {
}
