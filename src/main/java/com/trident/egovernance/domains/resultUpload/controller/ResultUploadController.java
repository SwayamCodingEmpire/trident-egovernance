package com.trident.egovernance.domains.resultUpload.controller;

import com.trident.egovernance.domains.resultUpload.services.ResultProcessingImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/exam")
public class ResultUploadController {

    @Autowired
    private ResultProcessingImpl resultProcessingImpl;

    private Logger logger = LoggerFactory.getLogger(ResultUploadController.class);

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("examType") String examType, @RequestParam("semester") int semester, @RequestParam("branch") String branch, @RequestParam("file") MultipartFile csvFile) {

        logger.info("Received file upload request for file: {}, examType: {}, semester: {}, branch: {}",
                csvFile.getOriginalFilename(), examType, semester, branch);

        if(csvFile.isEmpty()){
            logger.warn("Upload request received with an empty file.");
            return ResponseEntity.badRequest().body("Please select a csv file to upload");
        }

        try{
            resultProcessingImpl.processResultCSV(csvFile, examType, semester, branch);

            String successMessage = String.format("Successfully started processing results for %s, Semester %d (%s).", branch, semester, examType);
            return ResponseEntity.ok(successMessage);
        } catch (Exception e){
            return ResponseEntity.internalServerError().body("Failed to process file: " + e.getMessage());
        }
    }
}
