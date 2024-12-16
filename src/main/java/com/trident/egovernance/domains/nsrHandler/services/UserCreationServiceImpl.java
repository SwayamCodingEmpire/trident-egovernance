package com.trident.egovernance.domains.nsrHandler.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trident.egovernance.global.services.AppBearerTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserCreationServiceImpl implements UserCreationService {
    private final WebClient webClientGraph;
    private final AppBearerTokenService appBearerTokenService;
    private final Logger logger = LoggerFactory.getLogger(UserCreationServiceImpl.class);

    public UserCreationServiceImpl(AppBearerTokenService appBearerTokenService) {
        this.appBearerTokenService = appBearerTokenService;
        this.webClientGraph = WebClient.builder()
                .baseUrl("https://graph.microsoft.com/v1.0/users")
                .build();
    }



    @Override
    public String createUser(String displayName, String jobTitle, String department, String employeeId, String password, String email, int yop) {
        String appToken = appBearerTokenService.getAppBearerToken("defaultKey");

        String userPrincipalName = generateUserPrincipalName(displayName,department, yop);
        // Payload for creating the user
        logger.info("Creating user using app token: " + appToken);
        logger.info("Creating user using user principal: " + userPrincipalName);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accountEnabled", true);
        requestBody.put("displayName", displayName);
        requestBody.put("mailNickname", userPrincipalName.split("@")[0]);
        requestBody.put("jobTitle", jobTitle);
        requestBody.put("employeeId", employeeId);
        requestBody.put("department", department);
        requestBody.put("userPrincipalName", userPrincipalName);

        Map<String, Object> passwordProfile = new HashMap<>();
        passwordProfile.put("forceChangePasswordNextSignIn", true);
        passwordProfile.put("password", password);

        requestBody.put("passwordProfile", passwordProfile);

        logger.info("Maps: " + requestBody);
        String userPayload = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            userPayload = objectMapper.writeValueAsString(requestBody);  // Convert requestBody Map to JSON
            // Make the API call to create the user
            webClientGraph.post()
                    .header("Authorization", "Bearer " + appToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(userPayload)  // Dynamic user payload
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> {
                        // Handling error status code, return a Mono to propagate the error
                        return response.bodyToMono(String.class).flatMap(body -> {
                            logger.error("Error creating user: " + body);
                            return Mono.error(new RuntimeException("Failed to create user"));
                        });
                    })
                    .bodyToMono(String.class)
                    .block();
            return userPrincipalName;// Blocking call to wait for the response
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating JSON payload");
        }
    }

    private String generateUserPrincipalName(String fullName, String branch, int yearOfPassing) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty");
        }

        // Split the name into parts
        String[] nameParts = fullName.trim().toLowerCase().split("\\s+");
        if (nameParts.length < 2) {
            throw new IllegalArgumentException("Full name must contain at least first and last name");
        }

        // Extract first name and last name
        String firstName = String.join(" ", java.util.Arrays.copyOf(nameParts, nameParts.length - 1));
        String lastName = nameParts[nameParts.length - 1];

        // Combine parts to form UPN
        // Remove spaces in the first name

        return String.format(
                "%s.%s.%s%d@codingEmpire.onmicrosoft.com",
                firstName.replace(" ", ""), // Remove spaces in the first name
                lastName,
                branch.toLowerCase(),
                yearOfPassing);
    }
}
