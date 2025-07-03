package com.trident.egovernance.domains.resultUpload.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private JavaMailSender mailSender;

    public void beforeJob(JobExecution jobExecution) {
        logger.info("Job '{}' started with ID: {}", jobExecution.getJobInstance().getJobName(), jobExecution.getJobId());
    }

    public void afterJob(JobExecution jobExecution) {
        String userMail = jobExecution.getJobParameters().getString("userMail");
        String originalFileName = jobExecution.getJobParameters().getString("originalFileName");
        String tempFilePath = jobExecution.getJobParameters().getString("tempFilePath");

        String subject;
        String emailBody;

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            subject = "Exam Results Processing Completed Successfully";
            emailBody = String.format("Your Excel file '%s' has been processed successfully. Job ID: %d",
                    originalFileName != null ? originalFileName : "N/A", jobExecution.getJobId());
            logger.info("Job '{}' with ID {} completed successfully.", jobExecution.getJobInstance().getJobName(), jobExecution.getJobId());
        } else {
            subject = "Exam Result Processing Failed";
            String failureReason = jobExecution.getAllFailureExceptions().stream().map(Throwable::getMessage).collect(Collectors.joining("\n"));
            emailBody = String.format("There was an error processing your Excel file '%s'. Job ID: %d\nStatus: %s\nReason: %s",
                    originalFileName != null ? originalFileName : "N/A", jobExecution.getJobId(), jobExecution.getStatus(), failureReason);
            logger.error("Job '{}' with ID {} failed. Status: {}. Errors: {}", jobExecution.getJobInstance().getJobName(), jobExecution.getJobId(), jobExecution.getStatus(), failureReason);
        }

        if(userMail != null) {
            sendEmail(userMail, subject, emailBody);
        } else {
            logger.warn("No user email found in JobParameters for notification. Job ID: {}", jobExecution.getJobId());
        }

        if(tempFilePath != null) {
            try {
                Files.deleteIfExists(Paths.get(tempFilePath));
                logger.info("Cleaned up temporary file: {}", tempFilePath);
            } catch (IOException e){
                logger.error("Failed to delete temporary file {}: {}", tempFilePath, e.getMessage(), e);
            }
        }
    }

    private void sendEmail(String userEmail, String subject, String emailBody) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("mohantyswayam2001@gmail.com");
            message.setTo(userEmail);
            message.setSubject(subject);
            message.setText(emailBody);
            mailSender.send(message);
            logger.info("Notification email sent to {} for subject: {}", userEmail, subject);
        } catch (MailException e){
            logger.error("Failed to send email notification to {}: {}", userEmail, e.getMessage(), e);
        }
    }
}
