package com.trident.egovernance.domains.resultUpload.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JavaMailSender mailSender;

    public JobCompletionNotificationListener(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("Job '{}' started with ID: {}", jobExecution.getJobInstance().getJobName(), jobExecution.getJobId());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        String filePath = jobExecution.getJobParameters().getString("filePath");
        String originalFileName = jobExecution.getJobParameters().getString("originalFileName");
        String userEmail = jobExecution.getJobParameters().getString("userEmail");

        // Prepare email subject and body based on job status
        String subject;
        String emailBody;

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            logger.info("Job '{}' with ID {} COMPLETED successfully for file '{}'.",
                    jobExecution.getJobInstance().getJobName(), jobExecution.getId(), originalFileName);
            subject = "Exam Results Upload Completed Successfully";
            emailBody = String.format("Dear User,\n\nThe upload of exam results for file '%s' has been completed successfully.\n\nJob ID: %s\nStatus: %s\n\nThank you.",
                    originalFileName, jobExecution.getId(), jobExecution.getStatus());
            // ⭐ CALL sendEmail HERE for COMPLETED status ⭐
            if (userEmail != null && !userEmail.isEmpty()) {
                sendEmail(userEmail, subject, emailBody);
            } else {
                logger.warn("No user email found for successful job notification. Job ID: {}", jobExecution.getId());
            }

        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            logger.error("Job '{}' with ID {} FAILED. Status: FAILED. Errors: {}",
                    jobExecution.getJobInstance().getJobName(), jobExecution.getId(),
                    jobExecution.getAllFailureExceptions().isEmpty() ? "No specific error message found." : jobExecution.getAllFailureExceptions().get(0).getMessage());

            String failureMessages = jobExecution.getAllFailureExceptions().stream()
                    .map(Throwable::getMessage)
                    .collect(Collectors.joining("\n- ", "\nErrors:\n- ", ""));

            subject = "Exam Results Upload Failed!";
            emailBody = String.format("Dear User,\n\nThe upload of exam results for file '%s' has FAILED.\n\nJob ID: %s\nStatus: %s%s\n\nPlease check the application logs for more details or contact support.\n\nTrident eGovernance Team",
                    originalFileName, jobExecution.getId(), jobExecution.getStatus(), failureMessages);
            // ⭐ CALL sendEmail HERE for FAILED status ⭐
            if (userEmail != null && !userEmail.isEmpty()) {
                sendEmail(userEmail, subject, emailBody);
            } else {
                logger.warn("No user email found for failed job notification. Job ID: {}", jobExecution.getId());
            }

        } else {
            logger.warn("Job '{}' with ID {} finished with status: {}",
                    jobExecution.getJobInstance().getJobName(), jobExecution.getId(), jobExecution.getStatus());
            subject = "Exam Results Upload Status Update";
            emailBody = String.format("Dear User,\n\nThe upload of exam results for file '%s' has completed with status: %s.\n\nJob ID: %s\n\nPlease check the application logs for more details if the status is not COMPLETED or FAILED.\n\nThank you.\nTrident eGovernance Team",
                    originalFileName, jobExecution.getId(), jobExecution.getId());
            // ⭐ CALL sendEmail HERE for OTHER statuses if you want notifications ⭐
            if (userEmail != null && !userEmail.isEmpty()) {
                sendEmail(userEmail, subject, emailBody);
            } else {
                logger.warn("No user email found for job status update notification. Job ID: {}", jobExecution.getId());
            }
        }

        // ⭐ The file deletion logic remains outside the email sending if/else, as it should always run. ⭐
        if (filePath != null) {
            Path pathToDelete = Paths.get(filePath);
            if (Files.exists(pathToDelete)) {
                try {
                    Files.delete(pathToDelete);
                    logger.info("Successfully deleted temporary file: {}", filePath);
                } catch (IOException e) {
                    logger.warn("Failed to delete temporary file {}: {}", filePath, e.getMessage());
                }
            } else {
                logger.warn("Temporary file not found for deletion (already gone?): {}", filePath);
            }
        } else {
            logger.warn("No file path found in JobParameters for deletion. Job ID: {}", jobExecution.getId());
        }
    }

    private void sendEmail(String recipientEmail, String subject, String emailBody) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("mohantyswayam2001@gmail.com"); // Your sender email
            message.setTo(recipientEmail); // Use the dynamic recipientEmail
            message.setSubject(subject);
            message.setText(emailBody);
            mailSender.send(message);
            logger.info("Notification email sent to {} for subject: {}", recipientEmail, subject);
        } catch (MailException e){
            // Log the full stack trace for better debugging of mail issues
            logger.error("Failed to send email notification to {}: {}", recipientEmail, e.getMessage(), e);
        }
    }
}