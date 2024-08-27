package com.trident.egovernance.service;

import com.trident.egovernance.dto.AppBearerTokenDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserDataFetcherFromMS {
    private Logger logger = LoggerFactory.getLogger(UserDataFetcherFromMS.class);
    private final WebClient webClient;
    @Value("${trident.egovernance.client_id}")
    private String clientId;

    @Value("${trident.egovernance.client_secret}")
    private String clientSecret;
    @Value("${trident.egovernance.scope-uri}")
    private String scope_uri;

    public UserDataFetcherFromMS() {
        this.webClient = WebClient.builder().baseUrl("https://login.microsoftonline.com/df9a92cf-6a3b-4312-b0cd-28c580cf2804/oauth2/v2.0/token").build();
    }

    public Map<String, Object> getClaims() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info(SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString());
        return jwt.getClaims();
    }

    public String getAppBearerToken() {
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
}
