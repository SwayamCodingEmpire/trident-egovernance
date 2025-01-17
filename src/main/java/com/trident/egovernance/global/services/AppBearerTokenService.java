package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.AppBearerTokenDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AppBearerTokenService {
    private final WebClient webClient;
    @Value("${trident.egovernance.client_id}")
    private String clientId;

    @Value("${trident.egovernance.client_secret}")
    private String clientSecret;
    @Value("${trident.egovernance.scope-uri}")
    private String scope_uri;
    private final Logger logger = LoggerFactory.getLogger(AppBearerTokenService.class);

    public AppBearerTokenService(@Value("${spring.security.oauth2.client.provider.azure.token-uri}") String tokenUri) {
        this.webClient = WebClient.builder().baseUrl(tokenUri).build();
    }
    @Cacheable(key = "#defaultKey", value = "appBearerTokenCache")
    public String getAppBearerToken(String defaultKey) {
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
            logger.info("access_token: " + appBearerTokenDto.access_token());
            return appBearerTokenDto.access_token();
        }
        return "N/A";
    }

    @CachePut(key = "#defaultKey", value = "appBearerTokenCache")
    public String getAppBearerTokenForScheduler(String defaultKey) {
        try{
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
                logger.info("access_token: " + appBearerTokenDto.access_token());
                return appBearerTokenDto.access_token();
            }
            return "N/A";
        } catch (Exception e) {
            return getAppBearerTokenForScheduler(defaultKey);
        }
    }
}
