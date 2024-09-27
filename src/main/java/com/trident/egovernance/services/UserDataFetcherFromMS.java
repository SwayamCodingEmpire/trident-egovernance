package com.trident.egovernance.services;

import com.trident.egovernance.dto.AppBearerTokenDto;
import com.trident.egovernance.dto.UserJobInformationDto;
import com.trident.egovernance.exceptions.UserNotLoggedInException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class UserDataFetcherFromMS {
    private Logger logger = LoggerFactory.getLogger(UserDataFetcherFromMS.class);
    private final WebClient webClient;
    private final WebClient webClientGraph;
    @Value("${trident.egovernance.client_id}")
    private String clientId;

    @Value("${trident.egovernance.client_secret}")
    private String clientSecret;
    @Value("${trident.egovernance.scope-uri}")
    private String scope_uri;

    public UserDataFetcherFromMS() {
        this.webClientGraph = WebClient.builder().baseUrl("https://graph.microsoft.com/v1.0/users").build();
        this.webClient = WebClient.builder().baseUrl("https://login.microsoftonline.com/df9a92cf-6a3b-4312-b0cd-28c580cf2804/oauth2/v2.0/token").build();
    }

    public Map<String, Object> getClaims() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info(SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString());
        return jwt.getClaims();
    }

//    @Cacheable(value = "appBearerTokenCache")
    public String getAppBearerToken() {
        logger.info("Running getAppBearerToken");
        AppBearerTokenDto appBearerTokenDto = webClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials" +
                        "&client_id=" + clientId +
                        "&client_secret=" + clientSecret +
                        "&scope=" + scope_uri)
                .retrieve()
                .bodyToMono(AppBearerTokenDto.class)
                .block();
        logger.info("Bearer token fetched successfully");
        if (appBearerTokenDto != null) {
            logger.info("access_token: " + appBearerTokenDto.getAccess_token());
            return appBearerTokenDto.getAccess_token();
        }
        return "N/A";
    }

    public UserJobInformationDto getUserJobInformation(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated()){
            return fetchUserJobInformation(getAppBearerToken(),getClaims().get("preferred_username").toString());
        }
        throw new UserNotLoggedInException("User not logged in");
    }
//
//    @Cacheable(key = "#username",value = "userJobInformationCache")
    public UserJobInformationDto fetchUserJobInformation(String appToken,String username) {
        logger.info("Fetching the user job information");
        String uri = UriComponentsBuilder.fromPath("/"+username)
                .queryParam("$select","displayName,jobTitle,department")
                .toUriString();
        return webClientGraph.get()
                .uri(uri)
                .header("Authorization","Bearer "+appToken)
                .retrieve()
                .bodyToMono(UserJobInformationDto.class)
                .block();
    }
}
