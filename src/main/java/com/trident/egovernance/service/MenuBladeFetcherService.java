package com.trident.egovernance.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class MenuBladeFetcherService {
    private final Logger logger = LoggerFactory.getLogger(MenuBladeFetcherService.class);
    public List<String> getMenuBlade() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Inside getMenuBlade");
        if (authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            return fetchMenuBlade(authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        }
        return null;
    }

    private List<String> fetchMenuBlade(List<String> roles) {
        logger.info("Inside fetchMenuBlade");
        logger.info(roles.toString());
        List<String> menuBlade = new ArrayList<>();
        if (roles.get(0).compareTo("student") == 0) {
            menuBlade.add("Subjects");
            menuBlade.add("Attendance");
            menuBlade.add("SEMESTER RESULTS");
            menuBlade.add("Personal Activity Area");
            return menuBlade;
        } else if (roles.get(0).compareTo("faculty") == 0) {
                logger.info("Inside if block");
                menuBlade.add("Subjects");
                menuBlade.add("Take Attendance");
                menuBlade.add("Personal Activity Area");
                return menuBlade;
        }
        return null;
    }
}
