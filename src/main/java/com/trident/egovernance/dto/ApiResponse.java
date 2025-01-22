package com.trident.egovernance.dto;

import java.util.List;

public record ApiResponse(
        List<ObjectId> value
) {
}
