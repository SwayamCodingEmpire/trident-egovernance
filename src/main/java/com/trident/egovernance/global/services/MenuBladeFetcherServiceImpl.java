package com.trident.egovernance.global.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trident.egovernance.dto.MenuItem;
import com.trident.egovernance.dto.NavigationMenu;
import com.trident.egovernance.dto.RoleDetails;
import com.trident.egovernance.dto.UserJobInformationDto;
import com.trident.egovernance.exceptions.InvalidInputsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
public class MenuBladeFetcherServiceImpl implements MenuBladeFetcherService {

//    private final MenuBladeRepository menuBladeRepository;
    private final Logger logger = LoggerFactory.getLogger(MenuBladeFetcherServiceImpl.class);


    @Override
    @Cacheable(value = "Navigation")
    public NavigationMenu getNavigationMenu() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, RoleDetails> menuMap = mapper.readValue(
                    Paths.get("/home/likun10/Desktop/Menu.json").toFile(),
                    new TypeReference<Map<String, RoleDetails>>() {}
            );
            return new NavigationMenu(menuMap);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InvalidInputsException("Unable to read JSON file");
        }
    }

    @Override
    @CachePut(value = "Navigation")
    public NavigationMenu getNavigationMenuForceFetch() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, RoleDetails> menuMap = mapper.readValue(
                    Paths.get("/home/likun10/Desktop/Menu.json").toFile(),
                    new TypeReference<Map<String, RoleDetails>>() {}
            );
            return new NavigationMenu(menuMap);
        } catch (IOException e) {
            e.printStackTrace();
            throw new InvalidInputsException("Unable to read JSON file");
        }
    }
//
//    public MenuBladeFetcherServiceImpl(MenuBladeRepository menuBladeRepository) {
//        this.menuBladeRepository = menuBladeRepository;
//    }

//    @Override
//    @Cacheable(value = "menuBlade", key = "#authentication.name")
//    public MenuBladeDto getMenuBlade(Authentication authentication) {
//        logger.info("Inside getMenuBlade");
//        if (authentication.isAuthenticated()) {
//            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//            Optional<String> job_title = authorities.stream()
//                    .map(GrantedAuthority::getAuthority)
//                    .findFirst();
//            if(job_title.isPresent()){
//                return new MenuBladeDto(fetchMenuBlade(job_title.get()));
//            }
//            else {
//                throw new InvalidInputsException("Invalid job_title");
//            }
//        }
//        throw new AccessDeniedException("User not authenticated");
//    }

//    private List<String> fetchMenuBlade(String job_title) {
//        logger.info("Inside fetchMenuBlade");
//        logger.info(job_title);
//        return menuBladeRepository.findById(job_title).orElseThrow(()-> new InvalidInputsException("Invalid job_title")).getMenu_blade();
//    }
}
