package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.FeeCollection;

import java.util.List;

public record FeeCollectionHistoryDto(
        List<FeeCollectionAndMrDetails> year1,
        List<FeeCollectionAndMrDetails> year2,
        List<FeeCollectionAndMrDetails> year3,
        List<FeeCollectionAndMrDetails> year4
) {
}
