package com.trident.egovernance.dto;

import java.sql.Date;
import java.util.List;

public record SessionInitiationData(
        String prevSessionId,
        Date startDate,
        String sessionId,
        List<String> regdNos
) {
}
