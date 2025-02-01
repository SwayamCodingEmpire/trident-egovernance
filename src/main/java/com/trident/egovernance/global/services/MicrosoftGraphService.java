package com.trident.egovernance.global.services;


import com.trident.egovernance.dto.*;
import com.trident.egovernance.exceptions.RecordNotFoundException;
import com.trident.egovernance.global.entities.views.StInRoll;
import com.trident.egovernance.global.repositories.permanentDB.StudentRepository;
import com.trident.egovernance.global.repositories.views.StInRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class MicrosoftGraphService {
    private final AppBearerTokenService appBearerTokenService;
    private final Logger logger = LoggerFactory.getLogger(MicrosoftGraphService.class);
    private final WebClient webClientGraph;
    private final StudentRepository studentRepository;
    private final String token = "eyJ0eXAiOiJKV1QiLCJub25jZSI6InpwQlZTX0s0X2piV0NKckR4RFdaVmloaG5ZeW9nR3FHM2c0TXNVdUZxbnMiLCJhbGciOiJSUzI1NiIsIng1dCI6IllUY2VPNUlKeXlxUjZqekRTNWlBYnBlNDJKdyIsImtpZCI6IllUY2VPNUlKeXlxUjZqekRTNWlBYnBlNDJKdyJ9.eyJhdWQiOiJodHRwczovL2dyYXBoLm1pY3Jvc29mdC5jb20iLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC8wYjI0ZjY3MS1kNjE3LTQxMzgtODE5ZC0xNTcyYmIzN2ZjNGQvIiwiaWF0IjoxNzM3Mzc1MDAxLCJuYmYiOjE3MzczNzUwMDEsImV4cCI6MTczNzM3ODkwMSwiYWlvIjoiazJSZ1lDaFBTczJWWDNiZTNOYk5iZS83cnBldkFBPT0iLCJhcHBfZGlzcGxheW5hbWUiOiJuZXdFZ292ZXJuYW5jZSIsImFwcGlkIjoiMzRlOTdjMWQtMmJjZi00ZTgzLWE3NWEtYzhiNDI4OGRjZTJiIiwiYXBwaWRhY3IiOiIxIiwiaWRwIjoiaHR0cHM6Ly9zdHMud2luZG93cy5uZXQvMGIyNGY2NzEtZDYxNy00MTM4LTgxOWQtMTU3MmJiMzdmYzRkLyIsImlkdHlwIjoiYXBwIiwib2lkIjoiODk5OTFiYTQtYThhNy00ODI5LWE5MWQtYzVmMjg3Njg1MDUzIiwicmgiOiIxLkFWUUFjZllrQ3hmV09FR0JuUlZ5dXpmOFRRTUFBQUFBQUFBQXdBQUFBQUFBQUFDaUFBQlVBQS4iLCJyb2xlcyI6WyJVc2VyLlJlYWRXcml0ZS5BbGwiXSwic3ViIjoiODk5OTFiYTQtYThhNy00ODI5LWE5MWQtYzVmMjg3Njg1MDUzIiwidGVuYW50X3JlZ2lvbl9zY29wZSI6IkFTIiwidGlkIjoiMGIyNGY2NzEtZDYxNy00MTM4LTgxOWQtMTU3MmJiMzdmYzRkIiwidXRpIjoiazltV0NEOGNQMFNNdFVxcGpVQk5BQSIsInZlciI6IjEuMCIsIndpZHMiOlsiMDk5N2ExZDAtMGQxZC00YWNiLWI0MDgtZDVjYTczMTIxZTkwIl0sInhtc19pZHJlbCI6IjcgMTIiLCJ4bXNfdGNkdCI6MTQ4OTczMjkzOX0.WCLo5i5vdUq-X7Ag1f0QcWMA6CRRitORT_wKTvDo0fToq_rNKABsJPj_z-Sm6NU78QLw05oHAGNiHCDPxNXpvDojfQ3Fp4Z6z8FwN7P5srClKS3MgHTbrEw260eH6Hpzl-ZYI-E-i-1Q7P_5TZ4M8RwjCPLmzBhH5W1_y6u9I0uxqyY3PlHyvQc9O_K4lhanvgv8_A3JBxNVXvYsYimats7A_q9Vn_RfuccDhXyX-BNchBgaW4BddNJJpf7Pat4YEQnIfUfbTMU4b5oCWlVBfXrIy7Mfmpvgcd9X0IT8griwV7Z1lan_ElefZN-vREj5f0Z1AzSgdta7aND09i-SIA";
    private final StInRepository stInRepository;

    public MicrosoftGraphService(AppBearerTokenService appBearerTokenService, StudentRepository studentRepository, StInRepository stInRepository) {
        this.appBearerTokenService = appBearerTokenService;
        this.webClientGraph = WebClient.builder().baseUrl("https://graph.microsoft.com/v1.0").build();
        this.studentRepository = studentRepository;
        this.stInRepository = stInRepository;
    }

    public void getAllFileNames(String section){
        String folderPath = "/home/likun10/Desktop/1st year BPUT Registration wise DATA/" + section;// Replace with your folder path
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles(); // List all files and directories

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) { // Check if it is a file
                        String fileName = file.getName();
                        String filePath = file.getAbsolutePath();
                        logger.info(filePath);
                        if (fileName.contains(".")) {
                            fileName = fileName.substring(0, fileName.lastIndexOf('.')); // Remove extension
                        }
                        System.out.println("File name without extension: " + fileName);
                        StInRoll student = stInRepository.findById(fileName).orElseThrow(() -> new RecordNotFoundException("Student not found with regdNo"));
                        String jeeApplicationNo = student.getJeeApplicationNo();
                        logger.info(jeeApplicationNo);
                        logger.info(filePath);
                        startOps(jeeApplicationNo, filePath);
                    }
                }
            } else {
                System.out.println("No files found in the folder.");
            }
        } else {
            System.out.println("The specified path is not a directory.");
        }
    }

//    public void startOperation(String regdNo1, String regdNo2) {
//        List.of(regdNo1, regdNo2)
//                .forEach(this::fetchUserJobInformation);
//    }

    public void startOps(String jeeApplicationNo, String filePath) {
        logger.info("Fetching user job information for JEE Application Number: {}", jeeApplicationNo);

        String uri = UriComponentsBuilder.fromPath("/users")
                .queryParam("$filter", String.format("surname eq '%s'", jeeApplicationNo))
                .queryParam("$select", "id")
                .build(false)
                .toUriString();

        logger.info("Request URI: {}", uri);

        webClientGraph.get()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .flatMap(response -> {
                    List<ObjectId> value = response.value();
                    if (value != null && value.size() == 1) {
                        String id = value.get(0).id();
                        logger.info("Calling photoUpload with ID: {}", id);
                        return photoUpload(id, jeeApplicationNo, filePath);
                    } else if (value == null || value.isEmpty()) {
                        logger.error("Value array is empty for JEE Application No: {}", jeeApplicationNo);
                    } else {
                        logger.warn("Unexpected number of elements in value array: {}", value.size());
                    }
                    return Mono.empty();
                })
                .doOnError(error -> handleError(error,jeeApplicationNo))
                .subscribe();
    }

//    public Mono<Void> photoUpload(String oid) {
//        String photoPath = "/home/likun10/Desktop/sample_640×426.jpeg";
//        FileSystemResource fileResource = new FileSystemResource(photoPath);
//
//        logger.info("Uploading photo: {}", fileResource);
//        String uri = UriComponentsBuilder.fromPath("/" + oid + "/photo/$value")
//                .build(false)
//                .toUriString();
//
//
//        logger.info("Uploading photo to URI: {}", uri);
//
//        return webClientGraph.put()
//                .uri(uri)
//                .header("Authorization", "Bearer " + token)
//                .contentType(MediaType.IMAGE_JPEG)
//                .body(BodyInserters.fromResource(fileResource))
//                .retrieve()
//                .toBodilessEntity()
//                .doOnError(error -> logger.error("Error uploading photo for ID: {}", oid, error))
//                .doOnSuccess(response -> logger.info("Photo uploaded successfully for ID: {}", oid))
//                .then();
//    }
//
    private void handleError(Throwable error, String jeeApplicationNo) {
        if (error instanceof WebClientResponseException webError) {
            logger.info("Error in {}", jeeApplicationNo);
            logger.error("Error Response body: {}", webError.getResponseBodyAsString());
        } else {
            logger.error("Unexpected error: {}", error.getMessage(), error);
        }
    }

    public Mono<Void> photoUpload(String userId, String jeeApplicationNo, String filePath) {


        FileSystemResource fileResource = new FileSystemResource(filePath);

        // Build the URI for the API
        String uri = UriComponentsBuilder.fromPath("/users/" + userId + "/photo/$value").build(false).toUriString();

        return webClientGraph.put()
                .uri(uri)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.IMAGE_JPEG)  // Set the correct content type for the image
                .body(BodyInserters.fromResource(fileResource))  // Attach the file resource to the body
                .retrieve()
                .toBodilessEntity()  // Since we don't need a response body, we use toBodilessEntity()
                .doOnError(error -> handleError(error,jeeApplicationNo))
                .doOnSuccess(response -> logger.info("Photo uploaded successfully for jeeApplicationNo: {}", jeeApplicationNo))
                .then();  // Return Mono<Void> to indicate the operation is complete
    }

    public void sendEmailWithAttachment(String to, String subject, String htmlContent,
                                              byte[] pdfBytes, String accessToken) {
        // Create email attachment
        Message message = new Message(
                subject,
                new Body("HTML", htmlContent),
                List.of(new Recipient(new EmailAddress(to))),
                pdfBytes != null ? List.of(
                        new Attachment(
                                "#microsoft.graph.fileAttachment",
                                "Payment_Receipt.pdf",
                                "application/pdf",
                                Base64.getEncoder().encodeToString(pdfBytes)
                        )
                ) : List.of(),
                "normal"
        );

        // Create the request body exactly matching required structure
        Map<String, Object> requestBody = Map.of(
                "message", message,
                "saveToSentItems", true
        );

        webClientGraph
                .post()
                .uri("/me/sendMail")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Client error: " + error)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error)))
                )
                .bodyToMono(Void.class)
                .doOnSuccess(v -> logger.info("Email sent successfully"))
                .doOnError(e -> logger.error("Failed to send email", e))
                .block();
    }

    public byte[] getProfileImage(String oid){
        return webClientGraph.get()
                .uri("/users/"+oid+"/photo/$value")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + appBearerTokenService.getAppBearerToken("defaultKey"))
                .accept(MediaType.IMAGE_JPEG) // Expect an image
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }
}
