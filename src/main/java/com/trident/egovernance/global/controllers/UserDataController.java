package com.trident.egovernance.global.controllers;

import com.trident.egovernance.dto.TestingDto;
import com.trident.egovernance.dto.UserJobInformationDto;
import com.trident.egovernance.global.services.AppBearerTokenService;
import com.trident.egovernance.global.services.UserDataFetcherFromMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserDataController {
    private final Logger logger = LoggerFactory.getLogger(UserDataController.class);
    private final UserDataFetcherFromMS userDataFetcherFromMS;
    private final AppBearerTokenService appBearerTokenService;

    public UserDataController(UserDataFetcherFromMS userDataFetcherFromMS, AppBearerTokenService appBearerTokenService) {
        this.userDataFetcherFromMS = userDataFetcherFromMS;
        this.appBearerTokenService = appBearerTokenService;
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

    @GetMapping("/get-job-information")
    public ResponseEntity<UserJobInformationDto> getUserJobInformation(Authentication authentication){
        logger.info(authentication.getName());
        logger.info("Fetching the user job information");
        return ResponseEntity.ok(userDataFetcherFromMS.getUserJobInformation(authentication));
    }

}
