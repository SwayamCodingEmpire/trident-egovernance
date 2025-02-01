package com.trident.egovernance.dto;

import com.trident.egovernance.global.entities.permanentDB.Sections;
import com.trident.egovernance.global.helpers.Courses;

import java.util.List;

public record SectionFetcher(
        Courses course,
        Integer sem,
        String section,
        String branchCode,
        String sessionId,
        List<StudentSectionData> studentSectionData
) {
    public SectionFetcher(Sections sections, List<StudentSectionData> studentSectionData) {
        this(
                Courses.fromDisplayName(sections.getCourse()),
                sections.getSem(),
                sections.getSection(),
                sections.getBranchCode(),
                sections.getSessionId(),
                studentSectionData
        );
    }
}
