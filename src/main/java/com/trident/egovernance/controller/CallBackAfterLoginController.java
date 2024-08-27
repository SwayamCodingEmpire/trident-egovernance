package com.trident.egovernance.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trident.egovernance.dto.AuthorizationCodeDto;
import com.trident.egovernance.service.MenuBladeFetcherService;
import com.trident.egovernance.service.UserDataFetcherFromMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class CallBackAfterLoginController {

    @Value("${trident.client-id}")
    private String clientId;

    @Value("${trident.client-secret}")
    private String clientSecret;

    @Value("${trident.azure.redirect-uri}")
    private String redirectUri;

    @Value("${trident.provider.azure.token-uri}")
    private String tokenUri;

    private final Logger logger = LoggerFactory.getLogger(CallBackAfterLoginController.class);
    private final UserDataFetcherFromMS userDataFetcherFromMS;
    private final MenuBladeFetcherService menuBladeFetcherService;

    public CallBackAfterLoginController(UserDataFetcherFromMS userDataFetcherFromMS, MenuBladeFetcherService menuBladeFetcherService) {
        this.userDataFetcherFromMS = userDataFetcherFromMS;
        this.menuBladeFetcherService = menuBladeFetcherService;
    }

    @GetMapping("/oauth2/authorization/azure")
    public String testing(){
        return "Working";
    }

    @GetMapping("/oauth2/callback")
    public ResponseEntity<String> handleOAuth2Callback(AuthorizationCodeDto codeDto)throws IOException {
        String code = codeDto.getCode();
        logger.info(code);
        logger.info("Authorization Code {}",code);
        try {
            // Prepare the request body
            String requestBody = "grant_type=authorization_code"
                    + "&code=" + code
                    + "&redirect_uri=" + redirectUri
                    + "&client_id=" + clientId
                    + "&client_secret=" + clientSecret;

            // Create HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Create HTTP entity with headers and body
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            // Send POST request to get the access token
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    tokenUri,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            // Parse the response to get the JWT token
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseBody = objectMapper.readTree(response.getBody());
            String accessToken = responseBody.path("access_token").asText();

            // Log the token or use it as needed
            System.out.println("Access Token: " + accessToken);

            // Redirect or return as needed
            return ResponseEntity.ok(accessToken);
        }catch (Exception e){
            logger.error("Error : {}",e.getMessage());
            return ResponseEntity.ok("Error");
        }
    }
    @GetMapping("/test/myapi")
    public ResponseEntity<Map<String,Object>> testmyapi(){
        return ResponseEntity.ok(userDataFetcherFromMS.getClaims());
    }

    @GetMapping("/get-Menu_Blade")
    public ResponseEntity<List<String>> getMenuBlade(){
//        return ResponseEntity.ok(userDataFetcherFromMS.getMenuBlade());

        return ResponseEntity.ok(menuBladeFetcherService.getMenuBlade());
    }
}
