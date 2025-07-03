package com.trident.egovernance.domains.resultUpload.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class BatchProcessingImpl {
    @Autowired
    private JobLauncher jobLauncher;

    private Job importExamResultsJob;

    private static final Logger logger = LoggerFactory.getLogger(BatchProcessingImpl.class);

    @Async
    public void startResultProcessingJob(MultipartFile file, String examType, int semester, String branch, String academicYear, String userMail){
        Path tempFile = null;
        try{
            tempFile = Files.createTempFile("exam_result", ".xlsx");
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("filePath", tempFile.toAbsolutePath().toString())
                    .addString("examType", examType)
                    .addLong("semester", (long) semester)
                    .addString("branch", branch)
                    .addString("academicYear", academicYear)
                    .addString("userEmail", userMail)
                    .addString("originalFileName", file.getOriginalFilename())
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            logger.info("Successfully loaded the file: {}" ,tempFile.toAbsolutePath().toString());

            jobLauncher.run(importExamResultsJob, jobParameters);
        } catch (IOException e){
            logger.error("Error saving temporary file or reading input stream: {} ",e.getMessage());
        } catch (JobExecutionException e){
            logger.error("Failed to launch batch job: {}", e.getMessage());
        }
    }
}
