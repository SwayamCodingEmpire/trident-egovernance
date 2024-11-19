package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.views.CollectionReport;

import java.util.List;

public record CollectionReportDTO(
        CollectionReport collectionReport,
        List<MrDetailsDTO> mrDetailsDTO
) {
}
