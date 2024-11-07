package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.BasicMSUserDto;
import com.trident.egovernance.dto.UserJobInformationDto;
import com.trident.egovernance.exceptions.UserNotLoggedInException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class UserDataFetcherFromMS {
    private final AppBearerTokenService appBearerTokenService;
    private Logger logger = LoggerFactory.getLogger(UserDataFetcherFromMS.class);
    private final WebClient webClientGraph;

    public UserDataFetcherFromMS(AppBearerTokenService appBearerTokenService) {
        this.appBearerTokenService = appBearerTokenService;
        this.webClientGraph = WebClient.builder().baseUrl("https://graph.microsoft.com/v1.0/users").build();
    }

    public Map<String, Object> getClaims() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info(SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString());
        return jwt.getClaims();
    }

//    @Cacheable(key = "#defaultKey", value = "appBearerTokenCache")
//    public String getAppBearerToken(String defaultKey) {
//        logger.info("Running getAppBearerToken");
//        AppBearerTokenDto appBearerTokenDto = webClient.post()
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//                .bodyValue("grant_type=client_credentials" +
//                        "&client_id=" + clientId +
//                        "&client_secret=" + clientSecret +
//                        "&scope=" + scope_uri)
//                .retrieve()
//                .bodyToMono(AppBearerTokenDto.class)
//                .block();
//        logger.info("Bearer token fetched successfully");
//        if (appBearerTokenDto != null) {
//            logger.info("access_token: " + appBearerTokenDto.getAccess_token());
//            return appBearerTokenDto.getAccess_token();
//        }
//        return "N/A";
//    }



    @Cacheable(key = "#authentication.name", value = "userJobInformation")
    public UserJobInformationDto getUserJobInformation(Authentication authentication){
        logger.info("Running getUserJobInformation" + authentication.getName());
        if(authentication.isAuthenticated()){
            BasicMSUserDto basicMSUserDto = new BasicMSUserDto(appBearerTokenService.getAppBearerToken("defaultKey"),getClaims().get("preferred_username").toString());
            return fetchUserJobInformation(basicMSUserDto);
        }
        throw new UserNotLoggedInException("User not logged in");
    }
//

    @Cacheable(key = "#basicMSUserDto.username()", value = "UserJobInformationFilterData")
    public UserJobInformationDto fetchUserJobInformation(BasicMSUserDto basicMSUserDto) {
        logger.info("Fetching the user job information");
        String uri = UriComponentsBuilder.fromPath("/"+basicMSUserDto.username())
                .queryParam("$select","displayName,jobTitle,department")
                .toUriString();
        return webClientGraph.get()
                .uri(uri)
                .header("Authorization","Bearer "+basicMSUserDto.appToken())
                .retrieve()
                .bodyToMono(UserJobInformationDto.class)
                .block();
    }
}