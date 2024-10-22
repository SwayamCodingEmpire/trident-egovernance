package com.trident.egovernance.dto;

import java.util.List;

public record DuesDetailsSortedDto(
        List<DuesDetailsDto> year1,
        List<DuesDetailsDto> year2,
        List<DuesDetailsDto> year3,
        List<DuesDetailsDto> year4) {
}
