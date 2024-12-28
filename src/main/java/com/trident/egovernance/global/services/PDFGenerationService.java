package com.trident.egovernance.global.services;

import com.trident.egovernance.dto.PDFObject;
import com.trident.egovernance.exceptions.InvalidInputsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalTime;

@Service
public class PDFGenerationService {
    private final Logger logger = LoggerFactory.getLogger(PDFGenerationService.class);
    private final WebClient webClient;

    public PDFGenerationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://172.16.9.202:3000").build();
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
