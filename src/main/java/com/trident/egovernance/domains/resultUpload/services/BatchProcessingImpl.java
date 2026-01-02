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

import java.io.File;

@Service
public class BatchProcessingImpl {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importExamResultsJob;

    private static final Logger logger = LoggerFactory.getLogger(BatchProcessingImpl.class);

    @Async
    public void startResultProcessingJob(File file, String examType, int semester, String branch, String academicYear, String userMail, String originalFileName) {
//        Path filePathToDelete = file.toPath(); // Store path for deletion in finally block
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    // ⭐ CORRECTED LINE HERE ⭐
                    .addString("filePath", file.getAbsolutePath())
                    .addString("examType", examType)
                    .addLong("semester", (long) semester)
                    .addString("branch", branch)
                    .addString("academicYear", academicYear)
                    .addString("userEmail", userMail)
                    .addString("originalFileName", originalFileName)
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            logger.info("Successfully received managed temporary file: {}", file.getAbsolutePath()); // Also correct this log
            logger.info("Launching batch job with parameters for file: {}", originalFileName);

            jobLauncher.run(importExamResultsJob, jobParameters);

        } catch (JobExecutionException e) {
            logger.error("Failed to launch batch job for file {}. Error: {}", originalFileName, e.getMessage(), e);
        }
    }
}