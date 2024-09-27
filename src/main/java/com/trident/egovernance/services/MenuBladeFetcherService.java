package com.trident.egovernance.services;

import com.trident.egovernance.dto.MenuBladeDto;
import org.springframework.security.core.Authentication;

public interface MenuBladeFetcherService {
    MenuBladeDto getMenuBlade(Authentication authentication);

}
