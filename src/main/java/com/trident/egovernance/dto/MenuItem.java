package com.trident.egovernance.dto;

import java.util.List;

public record MenuItem(
        String title,
        String url,
        String logo,
        List<MenuItem> children
) {
}
