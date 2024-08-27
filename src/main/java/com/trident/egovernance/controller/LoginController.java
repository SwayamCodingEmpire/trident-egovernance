package com.trident.egovernance.controller;

import com.trident.egovernance.service.UserDataFetcherFromMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final UserDataFetcherFromMS userDataFetcherFromMS;

    public LoginController(UserDataFetcherFromMS userDataFetcherFromMS) {
        this.userDataFetcherFromMS = userDataFetcherFromMS;
    }

    @GetMapping("/login")
    public String welcome(){
        logger.info("Welcome to the OAuth Testing Controller");
        return "Welcome to the OAuth Testing Controller";
    }


    @GetMapping("/app-token")
    public String getAppBearerToken(){
        return userDataFetcherFromMS.getAppBearerToken();
    }
}
