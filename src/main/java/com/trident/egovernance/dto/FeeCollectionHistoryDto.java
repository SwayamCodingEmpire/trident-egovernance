package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.FeeCollection;

import java.util.List;

public record FeeCollectionHistoryDto(
        List<FeeCollection> year1,
        List<FeeCollection> year2,
        List<FeeCollection> year3,
        List<FeeCollection> year4
) {
}
