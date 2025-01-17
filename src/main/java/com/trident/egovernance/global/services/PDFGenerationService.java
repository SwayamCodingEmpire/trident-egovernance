package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.PDFObject;
import com.trident.egovernance.exceptions.InvalidInputsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalTime;

@Service
public class PDFGenerationService {
    private final Logger logger = LoggerFactory.getLogger(PDFGenerationService.class);
    private final WebClient webClient;

    public PDFGenerationService(WebClient.Builder webClientBuilder, @Value("${frontend.ip}") String frontEndIP) {
        this.webClient = webClientBuilder.baseUrl(frontEndIP).build();
    }

    public byte[] generatePdf(PDFObject requestBody) {
        try {
            logger.info("Generating PDF : {}", LocalTime.now());
            return webClient.post()
                    .uri("/api/generate-pdf")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .doOnError(e -> logger.error(e.getMessage()))
                    .block();
        } catch (WebClientResponseException e) {
            // Handle HTTP errors (e.g., 4xx, 5xx)
            throw new InvalidInputsException("PDF generation failed");
        } catch (Exception e) {
            // Handle other exceptions
            throw new InvalidInputsException("PDF generation failed");
        }
    }
}
