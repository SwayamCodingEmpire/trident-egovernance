package com.trident.egovernance.dto;

import java.util.List;
import java.util.Map;

public record NavigationMenu(
        Map<String, RoleDetails> menus
) {
}
