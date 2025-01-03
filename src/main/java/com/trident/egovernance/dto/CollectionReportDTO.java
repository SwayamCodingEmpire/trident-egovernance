package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.views.CollectionReport;
import com.trident.egovernance.global.entities.views.FeeCollectionView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record CollectionReportDTO(
        List<FeeCollectionView> collectionReport,
        Map<String, BigDecimal> desciptionSum
) {
}
