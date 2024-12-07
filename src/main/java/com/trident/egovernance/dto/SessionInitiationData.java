package com.trident.egovernance.dto;

import com.trident.egovernance.global.helpers.Courses;
import com.trident.egovernance.global.helpers.StudentType;

import java.sql.Date;
import java.util.Set;

public record SessionInitiationData(
        Integer admYear,
        String prevSessionId,
        Date startDate,
        String sessionId,
        Courses course,
        Set<String> regdNos,
        StudentType studentType,
        String branchCode,
        int currentYear
) {
}
