package com.trident.egovernance.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class UserDataFetcherFromMS {
    private final WebClient webClient;
    @Value("${spring.security.oauth2.client.provider.azure.token-uri}")
    private String tokenUri;

    public UserDataFetcherFromMS() {
        this.webClient = WebClient.builder().baseUrl(tokenUri).build();
    }

    public Map<String, Object> getClaims() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getClaims();
    }

    public void getAppBearerToken() {
        webClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials" +
                        "&client_id=client_id" +
                        "&client_secret=client_secret" +
                        "&scope=https://graph.microsoft.com/.default");
    }
}
