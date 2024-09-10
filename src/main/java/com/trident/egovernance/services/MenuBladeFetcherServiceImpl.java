package com.trident.egovernance.services;

import com.trident.egovernance.dtos.MenuBladeDto;
import com.trident.egovernance.exceptions.InvalidInputsException;
import com.trident.egovernance.repositories.redisRepositories.MenuBladeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class MenuBladeFetcherServiceImpl implements MenuBladeFetcherService {
    private final MenuBladeRepository menuBladeRepository;
    private final Logger logger = LoggerFactory.getLogger(MenuBladeFetcherServiceImpl.class);

    public MenuBladeFetcherServiceImpl(MenuBladeRepository menuBladeRepository) {
        this.menuBladeRepository = menuBladeRepository;
    }

    @Override
    @Cacheable(value = "menuBlade", key = "#authentication.getPrincipal()")
    public MenuBladeDto getMenuBlade(Authentication authentication) {
        logger.info("Inside getMenuBlade");
        if (authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            Optional<String> job_title = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst();
            if(job_title.isPresent()){
                return new MenuBladeDto(fetchMenuBlade(job_title.get()));
            }
            else {
                throw new InvalidInputsException("Invalid job_title");
            }
        }
        throw new AccessDeniedException("User not authenticated");
    }

    private List<String> fetchMenuBlade(String job_title) {
        logger.info("Inside fetchMenuBlade");
        logger.info(job_title);
        return menuBladeRepository.findById(job_title).orElseThrow(()-> new InvalidInputsException("Invalid job_title")).getMenu_blade();
    }
}
