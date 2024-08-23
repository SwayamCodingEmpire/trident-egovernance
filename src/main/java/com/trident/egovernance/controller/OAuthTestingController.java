package com.trident.egovernance.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class OAuthTestingController {
    private final Logger logger = LoggerFactory.getLogger(OAuthTestingController.class);

    @GetMapping("/login")
    public String welcome(){
        logger.info("Welcome to the OAuth Testing Controller");
        return "Welcome to the OAuth Testing Controller";
    }

    @GetMapping("/oauth2/callback")
    public String handleOAuth2Callback(@RequestParam(name = "code") String code,@RequestParam(name = "state") String state , @RequestParam(name = "session_state",required = false) String sessionState)throws IOException{
        logger.info(code);
        logger.info("Authorization Code {}",code);
        logger.info("State : {}",state);
        logger.info("Session State : {}",sessionState);
        return "redirect:https://www.google.com";
    }
}
