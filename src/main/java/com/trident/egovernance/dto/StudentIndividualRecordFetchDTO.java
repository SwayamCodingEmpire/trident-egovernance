package com.trident.egovernance.dto;

import java.util.List;

public record StudentIndividualRecordFetchDTO(
        StudentOnlyDTO studentOnlyDTO,
        PersonalDetailsOnlyDTO personalDetailsOnlyDTO,
        StudentAdmissionDetailsOnlyDTO studentAdmissionDetailsOnlyDTO,
        StudentCareerOnlyDTO studentCareerOnlyDTO,
        HostelOnlyDTO hostelOnlyDTO,
        TransportOnlyDTO transportOnlyDTO,
        List<StudentDocsOnlyDTO> studentDocsOnlyDTOS
) {
}
