package com.trident.egovernance.global.controllers;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.services.AppBearerTokenService;
import com.trident.egovernance.global.services.MiscellaniousServices;
import com.trident.egovernance.global.services.ProfileFetcherService;
import com.trident.egovernance.global.services.UserDataFetcherFromMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserDataController {
    private final ProfileFetcherService profileFetcherService;
    private final Logger logger = LoggerFactory.getLogger(UserDataController.class);
    private final UserDataFetcherFromMS userDataFetcherFromMS;
    private final AppBearerTokenService appBearerTokenService;
    private final MiscellaniousServices miscellaniousServices;

    public UserDataController(ProfileFetcherService profileFetcherService, UserDataFetcherFromMS userDataFetcherFromMS, AppBearerTokenService appBearerTokenService, MiscellaniousServices miscellaniousServices) {
        this.profileFetcherService = profileFetcherService;
        this.userDataFetcherFromMS = userDataFetcherFromMS;
        this.appBearerTokenService = appBearerTokenService;
        this.miscellaniousServices = miscellaniousServices;
    }

    @GetMapping("/login")
    public ResponseEntity<String> welcome(){
        logger.info("Welcome to the OAuth Testing Controller");
        return ResponseEntity.ok("Welcome to the OAuth Testing Controller");
    }


    @GetMapping("/app-token")
    public ResponseEntity<TestingDto> getAppBearerToken(){
        logger.info("Fetching the app bearer token");
        TestingDto testingDto = new TestingDto();
        testingDto.setAppToken(appBearerTokenService.getAppBearerToken("defaultKey"));
        logger.info("App token : {} ",testingDto.getAppToken());
        return ResponseEntity.ok(testingDto);
    }

    @GetMapping("/get-user-information")
    public ResponseEntity<ProfileDTO> getUserJobInformation(Authentication authentication){
        logger.info(authentication.getName());
        logger.info("Fetching the user job information");
        return ResponseEntity.ok(profileFetcherService.getUserJobInformation(authentication));
    }

    @GetMapping("/get-menu-blade")
    public ResponseEntity<RoleDetails> getMenuBlade(){
        return ResponseEntity.ok(miscellaniousServices.getMenuItems());
    }

}
