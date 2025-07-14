package com.trident.egovernance.domains.resultUpload.controller;

import com.trident.egovernance.domains.resultUpload.services.BatchProcessingImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
// Removed security imports as userEmail will be a parameter

@RestController
@RequestMapping("/exam")
public class ResultUploadController {

    @Autowired
    private BatchProcessingImpl batchProcessingImpl;

    // You can keep the constructor or rely solely on @Autowired field injection
    public ResultUploadController(BatchProcessingImpl batchProcessingImpl) {
        this.batchProcessingImpl = batchProcessingImpl;
    }

    private final Logger logger = LoggerFactory.getLogger(ResultUploadController.class);

    @PostMapping("/upload-results")
    public ResponseEntity<String> handleFileUpload(
            @RequestParam("examType") String examType,
            @RequestParam("semester") int semester,
            @RequestParam("branch") String branch,
            @RequestParam("academicYear") String academicYear,
            @RequestParam("file") MultipartFile csvFile,
            @RequestParam("notificationEmail") String notificationEmail) { // ⭐ NEW PARAMETER HERE ⭐

        // ⭐ REMOVE OR COMMENT OUT THE ENTIRE USER EMAIL EXTRACTION LOGIC ⭐
        // String userEmail = null;
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // if (authentication != null && authentication.isAuthenticated()) {
        //     // ... (your existing user email extraction logic) ...
        // }

        // Use the provided notificationEmail directly
        if (notificationEmail == null || notificationEmail.isEmpty()) {
            logger.error("Notification email not provided. Cannot proceed with batch processing.");
            return ResponseEntity.badRequest().body("Notification email is required.");
        }

        logger.info("Received file upload request:");
        logger.info("  File Name: {}", csvFile.getOriginalFilename());
        logger.info("  Exam Type: {}", examType);
        logger.info("  Semester: {}", semester);
        logger.info("  Branch: {}", branch);
        logger.info("  Academic Year: {}", academicYear);
        logger.info("  File Size: {} bytes", csvFile.getSize());
        logger.info("  File Content Type: {}", csvFile.getContentType());
        logger.info("  Notification Email: {}", notificationEmail); // Log the provided email

        if (csvFile.isEmpty()) {
            logger.warn("Upload request received with an empty file.");
            return ResponseEntity.badRequest().body("Please select a csv file to upload");
        }

        Path managedTempFile = null;
        try {
            managedTempFile = Files.createTempFile("exam_upload_", "_" + csvFile.getOriginalFilename());
            Files.copy(csvFile.getInputStream(), managedTempFile, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Successfully copied uploaded file to managed temporary path: {}", managedTempFile.toAbsolutePath());

            String successMessage = String.format(
                    "Batch processing job started successfully for file '%s'. You will receive an email notification at '%s' upon completion.",
                    csvFile.getOriginalFilename(), notificationEmail); // Use notificationEmail here

            // Pass the notificationEmail to the service method
            batchProcessingImpl.startResultProcessingJob(
                    managedTempFile.toFile(),
                    examType, semester, branch, academicYear, notificationEmail, csvFile.getOriginalFilename()); // ⭐ Pass notificationEmail ⭐

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(successMessage);

        } catch (IOException e) {
            logger.error("Failed to save or process uploaded file locally: {}", e.getMessage(), e);
            if (managedTempFile != null && Files.exists(managedTempFile)) {
                try {
                    Files.delete(managedTempFile);
                    logger.info("Cleaned up partially created temp file after failure: {}", managedTempFile.toAbsolutePath());
                } catch (IOException cleanupEx) {
                    logger.warn("Failed to clean up partially created temp file: {}", managedTempFile.toAbsolutePath(), cleanupEx);
                }
            }
            return ResponseEntity.internalServerError().body("Failed to process file: " + e.getMessage());
        }
    }
}