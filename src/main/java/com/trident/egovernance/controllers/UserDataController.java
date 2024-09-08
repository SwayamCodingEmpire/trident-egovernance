package com.trident.egovernance.controllers;

import com.trident.egovernance.dtos.TestingDto;
import com.trident.egovernance.dtos.UserJobInformationDto;
import com.trident.egovernance.services.UserDataFetcherFromMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserDataController {
    private final Logger logger = LoggerFactory.getLogger(UserDataController.class);
    private final UserDataFetcherFromMS userDataFetcherFromMS;

    public UserDataController(UserDataFetcherFromMS userDataFetcherFromMS) {
        this.userDataFetcherFromMS = userDataFetcherFromMS;
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
        testingDto.setAppToken(userDataFetcherFromMS.getAppBearerToken());
        logger.info("App token : {} ",testingDto.getAppToken());
        return ResponseEntity.ok(testingDto);
    }

    @GetMapping("/get-job-information")
    public ResponseEntity<UserJobInformationDto> getUserJobInformation(){
        logger.info("Fetching the user job information");
        return ResponseEntity.ok(userDataFetcherFromMS.getUserJobInformation());
    }
}
