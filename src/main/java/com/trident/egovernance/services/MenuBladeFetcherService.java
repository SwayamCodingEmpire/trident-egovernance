package com.trident.egovernance.services;

import com.trident.egovernance.dtos.MenuBladeDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface MenuBladeFetcherService {
    MenuBladeDto getMenuBlade(Authentication authentication);

}
