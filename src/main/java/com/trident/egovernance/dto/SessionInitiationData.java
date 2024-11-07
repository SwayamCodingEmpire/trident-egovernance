package com.trident.egovernance.dto;

import java.sql.Date;
import java.util.List;
import java.util.Set;

public record SessionInitiationData(
        String prevSessionId,
        Date startDate,
        String sessionId,
        Set<String> regdNos,
        int currentYear
) {
}
