package com.trident.egovernance.global.controllers;

import com.trident.egovernance.global.entities.permanentDB.Subject_Details;
import com.trident.egovernance.global.helpers.SubjectInfo;
import com.trident.egovernance.global.services.SubjectDataFetcherService;
import com.trident.egovernance.global.services.SubjectTempDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/subjects")
public class SubjectController {
    private final Logger logger = LoggerFactory.getLogger(SubjectController.class);
    private final SubjectDataFetcherService subjectDataFetcherService;
    private static final Pattern SUBJECT_ROW_PATTERN = Pattern.compile(
            "<tr[^>]*>\\s*" +
                    "<td[^>]*>(\\d+)</td>\\s*" +
                    "<td>([^<]+)</td>\\s*" +
                    "<td>([^<]+)</td>\\s*" +
                    "<td>([^<]+)</td>\\s*" +
                    "<td>([^<]*)</td>\\s*" +
                    "<td>([^<]*)</td>\\s*" +
                    "(?:<td><a href=\"([^\"]+)\"[^>]*>view</a></td>)?"
    );

    public SubjectController(SubjectDataFetcherService subjectDataFetcherService) {
        this.subjectDataFetcherService = subjectDataFetcherService;
    }

    @PostMapping(value = "/upload-subjects", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadSubjectFile(@RequestParam("file") MultipartFile file) {
        // Validate file is not empty
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("File is empty. Please upload a valid HTML file.");
        }

        // Validate file size (e.g., limit to 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                    .body("File too large. Maximum file size is 10MB.");
        }

        try {
            // Validate file content type (optional, but recommended)
            String contentType = file.getContentType();
            if (contentType != null &&
                    !contentType.contains("text/html") &&
                    !contentType.contains("text/plain")) {
                return ResponseEntity.badRequest()
                        .body("Invalid file type. Please upload an HTML or text file.");
            }

            // Read file content with explicit character encoding
            String fileContent = new String(
                    file.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            // Validate file content is not empty after reading
            if (fileContent.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("File content is empty.");
            }

            // Parse subjects using service
            List<SubjectInfo> subjects = subjectDataFetcherService.parseSubjectData(fileContent);

            // Validate parsed subjects
            if (subjects.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("No subjects could be parsed from the file.");
            }

            // Return parsed subjects with additional metadata
            return ResponseEntity.ok()
                    .header("X-Total-Count", String.valueOf(subjects.size()))
                    .body(subjects);

        } catch (IOException e) {
            // Log the error for server-side tracking
            logger.error("Error processing uploaded file", e);

            // Return a more informative error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process the file: " + e.getMessage());
        } catch (Exception e) {
            // Catch any unexpected exceptions during parsing
            logger.error("Unexpected error during subject parsing", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/save-subjects")
    public ResponseEntity<Boolean> saveSubjects(@RequestBody List<SubjectTempDTO> subjects) {
        subjectDataFetcherService.saveSubjectDetails(subjects);
        return ResponseEntity.ok(true);
    }
}
