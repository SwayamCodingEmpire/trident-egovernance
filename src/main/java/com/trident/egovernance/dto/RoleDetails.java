package com.trident.egovernance.dto;

import java.io.Serializable;
import java.util.List;

public record RoleDetails(
        String redirectUrl,
        List<String> allowedRoutes,
        List<MenuItem> urls
) implements Serializable {
}
