package com.trident.egovernance.domains.resultUpload.controller;

import com.trident.egovernance.domains.resultUpload.services.BatchProcessingImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/exam")
public class ResultUploadController {

    @Autowired
    private BatchProcessingImpl batchProcessingImpl;

    public ResultUploadController(BatchProcessingImpl batchProcessingImpl) {
        this.batchProcessingImpl = batchProcessingImpl;
    }

    private Logger logger = LoggerFactory.getLogger(ResultUploadController.class);

    @PostMapping("/upload-results")
    public ResponseEntity<String> handleFileUpload(@RequestParam("examType") String examType, @RequestParam("semester") int semester, @RequestParam("branch") String branch, @RequestParam("academicYear") String academicYear, @RequestParam("file") MultipartFile csvFile) {

        String userEmail = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof OidcUser) {
                OidcUser oidcUser = (OidcUser) principal;
                userEmail = oidcUser.getEmail();
                if (userEmail == null || userEmail.isEmpty()) {
                    userEmail = oidcUser.getName();
                }
                logger.info("Received user email (OIDC): {}", userEmail);
            } else if (principal instanceof OAuth2User) {
                OAuth2User oauth2User = (OAuth2User) principal;
                userEmail = oauth2User.getAttribute("email");
                if (userEmail == null || userEmail.isEmpty()) {
                    userEmail = oauth2User.getName();
                }
                logger.info("Retrieved user email (OAuth2): {}", userEmail);
            } else if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                userEmail = userDetails.getUsername();
                logger.info("Retrieved user email (UserDetails): {}", userEmail);
            } else if (principal instanceof String) {
                userEmail = (String) principal;
                logger.info("Retrieved user email (String principal): {}", userEmail);
            } else {
                logger.warn("Unknown principal type: {}. Cannot extract email automatically.", principal.getClass().getName());
            }
        }

        if (userEmail == null || userEmail.isEmpty()) {
            logger.error("Could not determine user's email from security context. Cannot proceed with batch processing.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User email could not be determined. Please log in again.");
            // Or, if email is optional for some cases, you could use a default or log a warning and proceed.
        }

        logger.info("Received file upload request:");
        logger.info("  File Name: {}", csvFile.getOriginalFilename());
        logger.info("  Exam Type: {}", examType);
        logger.info("  Semester: {}", semester);
        logger.info("  Branch: {}", branch);
        logger.info("  Academic Year: {}", academicYear); // Log the new parameter
        logger.info("  File Size: {} bytes", csvFile.getSize());
        logger.info("  File Content Type: {}", csvFile.getContentType());

        logger.info("Received file upload request for file: {}, examType: {}, semester: {}, branch: {}, academicYear: {}",
                csvFile.getOriginalFilename(), examType, semester, branch, academicYear);

        if (csvFile.isEmpty()) {
            logger.warn("Upload request received with an empty file.");
            return ResponseEntity.badRequest().body("Please select a csv file to upload");
        }

        try {
            batchProcessingImpl.startResultProcessingJob(csvFile, examType, semester, branch, academicYear, userEmail);

            String successMessage = String.format(
                    "Batch processing job started successfully for file '%s'. You will receive an email notification at '%s' upon completion.",
                    csvFile.getOriginalFilename(), userEmail);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(successMessage);
        } catch (Exception e) {
            logger.error("Failed to start batch processing job for file {}: {}", csvFile.getOriginalFilename(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to initiate file processing: " + e.getMessage());
        }
    }
}
