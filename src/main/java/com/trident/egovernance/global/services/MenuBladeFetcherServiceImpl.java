package com.trident.egovernance.global.services;

import org.springframework.stereotype.Service;

@Service
public class MenuBladeFetcherServiceImpl implements MenuBladeFetcherService {
//    private final MenuBladeRepository menuBladeRepository;
//    private final Logger logger = LoggerFactory.getLogger(MenuBladeFetcherServiceImpl.class);
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
