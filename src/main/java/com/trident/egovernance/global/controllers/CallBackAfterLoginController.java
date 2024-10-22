package com.trident.egovernance.global.controllers;


import com.trident.egovernance.global.services.MenuBladeFetcherServiceImpl;
import com.trident.egovernance.global.services.UserDataFetcherFromMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/menu")
public class CallBackAfterLoginController {

    @Value("${spring.security.oauth2.client.registration.azure.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.azure.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.azure.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.azure.token-uri}")
    private String tokenUri;

    private final Logger logger = LoggerFactory.getLogger(CallBackAfterLoginController.class);
    private final UserDataFetcherFromMS userDataFetcherFromMS;
    private final MenuBladeFetcherServiceImpl menuBladeFetcherServiceImpl;

    public CallBackAfterLoginController(UserDataFetcherFromMS userDataFetcherFromMS, MenuBladeFetcherServiceImpl menuBladeFetcherServiceImpl) {
        this.userDataFetcherFromMS = userDataFetcherFromMS;
        this.menuBladeFetcherServiceImpl = menuBladeFetcherServiceImpl;
    }

    @GetMapping("/oauth2/authorization/azure")
    public String testing(){
        return "Working";
    }

//    @GetMapping("/oauth2/callback")
//    public ResponseEntity<String> handleOAuth2Callback(@RequestParam(name = "code") String code, @RequestParam(name = "state") String state , @RequestParam(name = "session_state",required = false) String sessionState )throws IOException {
//        logger.info(code);
//        logger.info("Authorization Code {}",code);
//        try {
//            // Prepare the request body
//            String requestBody = "grant_type=authorization_code"
//                    + "&code=" + code
//                    + "&redirect_uri=" + redirectUri
//                    + "&client_id=" + clientId
//                    + "&client_secret=" + clientSecret;
//
//            // Create HTTP headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            // Create HTTP entity with headers and body
//            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
//
//            // Send POST request to get the access token
//            RestTemplate restTemplate = new RestTemplate();
//            ResponseEntity<String> response = restTemplate.exchange(
//                    tokenUri,
//                    HttpMethod.POST,
//                    request,
//                    String.class
//            );
//
//            // Parse the response to get the JWT token
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode responseBody = objectMapper.readTree(response.getBody());
//            String accessToken = responseBody.path("access_token").asText();
//
//            // Log the token or use it as needed
//            System.out.println("Access Token: " + accessToken);
//
//            // Redirect or return as needed
//            return ResponseEntity.ok(accessToken);
//        }catch (Exception e){
//            logger.error("Error : {}",e.getMessage());
//            return ResponseEntity.ok("Error");
//        }
//    }
    @GetMapping("/test/myapi")
    public ResponseEntity<Map<String,Object>> testmyapi(){
        return ResponseEntity.ok(userDataFetcherFromMS.getClaims());
    }

//    @GetMapping("/get-Menu_Blade")
//    public ResponseEntity<MenuBladeDto> getMenuBlade(){
//        return ResponseEntity.ok(menuBladeFetcherServiceImpl.getMenuBlade(SecurityContextHolder.getContext().getAuthentication()));
//    }
}
