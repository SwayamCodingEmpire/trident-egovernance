package com.trident.egovernance.controllers;

import com.trident.egovernance.config.security.CustomUserDetails;
import com.trident.egovernance.dtos.Login;
import com.trident.egovernance.dtos.LoginResponse;
import com.trident.egovernance.services.AuthenticationServiceImpl;
import com.trident.egovernance.services.CustomJwtServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {
    private final AuthenticationServiceImpl authenticationService;
    private final CustomJwtServiceImpl customJwtService;
    private final Logger logger = LoggerFactory.getLogger(PublicController.class);

    public PublicController(AuthenticationServiceImpl authenticationService, CustomJwtServiceImpl customJwtService) {
        this.authenticationService = authenticationService;
        this.customJwtService = customJwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Login login){
        CustomUserDetails customUserDetails = (CustomUserDetails) authenticationService.authenticate(login);
        logger.info(customUserDetails.toString());
        return ResponseEntity.ok(new LoginResponse(customJwtService.generateToken(customUserDetails), customJwtService.getExpirationTime()));
    }
}
