package com.trident.egovernance.domains.nsrHandler.services;

import com.trident.egovernance.dto.*;
import com.trident.egovernance.global.services.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailSenderServiceImpl {
    private final WebClient webClientMailer;
    private final MoneyReceiptPDFGenerator moneyReceiptPDFGenerator;
    private final Logger logger = LoggerFactory.getLogger(EmailSenderServiceImpl.class);
    private final JavaMailSender mailSender;
    private final PDFGenerationService pdfGenerationService;
    private final AppBearerTokenService appBearerTokenService;
    private final MiscellaniousServices miscellaniousServices;
    private final MicrosoftGraphService microsoftGraphService;

    public EmailSenderServiceImpl(MoneyReceiptPDFGenerator moneyReceiptPDFGenerator, JavaMailSender mailSender, PDFGenerationService pdfGenerationService, AppBearerTokenService appBearerTokenService, MiscellaniousServices miscellaniousServices, MicrosoftGraphService microsoftGraphService) {
        this.webClientMailer = WebClient.builder().baseUrl("https://graph.microsoft.com/v1.0")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.moneyReceiptPDFGenerator = moneyReceiptPDFGenerator;
        this.mailSender = mailSender;
        this.pdfGenerationService = pdfGenerationService;
        this.appBearerTokenService = appBearerTokenService;
        this.miscellaniousServices = miscellaniousServices;
        this.microsoftGraphService = microsoftGraphService;
    }

    @Async
    public CompletableFuture<Void> sendTridentCredentialsEmail(String microsoftMail, String password) throws MessagingException, IOException {
        logger.info("Sending Trident credentials email");
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String inductionStart = "21st October 2024";
        String inductionTimeStart = "11AM";
        String inductionTimeEnd = "1PM";
        String inductionEnd = "25th October 2024";
        String classStart = "1st November 2024";
        // Sender and Recipient
        helper.setFrom("mohantyswayam2001@gmail.com");
        helper.setTo("elitecracker25@gmail.com");
        helper.setSubject("Trident : Credentials for Microsoft Teams");

        // HTML Content with colored text
        String emailContent =
                "<html><body>" +
                        "<p>Dear Student,</p>" +

                        "<p>The mode of delivery of online classes will be via <span style='color:blue;'>Microsoft Teams</span>. " +
                        "Please find below your credentials to login to Microsoft Teams. " +
                        "<span style='color:red;'>Only logged in students can join their respective lectures.</span></p>" +

                        "<p>Your Username: <b style='color:blue;'><a href = \"mailto:"+ microsoftMail + "\">" + microsoftMail + "</a></b><br>" +
                        "Your Password: <b style='color:blue;'>"+ password +"</b></p>" +

                        "<p><span style='color:red;'>*Please do not disclose this information to others. " +
                        "Anyone doing disturbances using your credentials will be treated as to be done by you.</span></p>" +

                        "<p>Please find attached step by step help manuals to join the online classes.</p>" +

                        "<p>Induction classes will be held from <b>" + inductionStart + "</b> to <b>" + inductionEnd + "</b> barring Sunday. " +
                        "Induction class timings will be "+ inductionTimeStart +" to"+ inductionTimeEnd + ".</p>" +

                        "<p>Regular classes will start from <b>" + classStart + "</b>. Timetable will be shared later.</p>" +

                        "<p>Best Regards,<br>Sumanta Sahoo<br>DGM (Technical)<br>Trident Group of Institutions</p>" +

                        "<p>Note:<br>" +
                        "1. For Sign in issues Whatsapp Mr. Nikhil Lenka, System Administrator @ " +
                        "<a href='tel:9124078910'>9124078910</a></p>" +

                        "</body></html>";

        helper.setText(emailContent, true);

        // Attach PDF files
//        helper.addAttachment("stepstojoinonlineclassandroid.pdf",
//                new ClassPathResource("stepstojoinonlineclassandroid.pdf"));
//        helper.addAttachment("stepstojoinonlineclassbrowser.pdf",
//                new ClassPathResource("stepstojoinonlineclassbrowser.pdf"));

        // Send the microsoftMail
        mailSender.send(message);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> sendPaymentReceiptEditEmail(
            String recipientEmail, Long mrNumber, String name, BigDecimal amountPaid, String supportPhoneNumber, PDFObject pdfObject, String graphToken, Long oldMrNo)
            throws MessagingException, IOException {
//        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//        Jwt jwt = authentication.getToken();
//        Jwt jwts = (Jwt)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//        Map<String,Object> claims = jwts.getClaims();
        byte[] receiptPdfBytes = pdfGenerationService.generatePdf(pdfObject);

        logger.info("Generated receipt pdf bytes");
        logger.info("receiptPdfBytes: " + receiptPdfBytes);
        logger.info("Generated PDF : {}", LocalTime.now());
        // Create a MIME message
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//        // Set sender and recipient
//        helper.setFrom("mohantyswayam2001@gmail.com");
//        helper.setTo("elitecracker25@gmail.com");
//        helper.setSubject("Payment Receipt");

        // Email HTML content with dynamic data
        String emailContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>Payment Receipt</title>" +
                "<style>" +
                "body {font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #121212; color: #ffffff;}" +
                ".email-container {max-width: 35rem; margin: 5rem auto; background-color: #1e1e1e; border: 0.3rem solid #333333; padding: 1rem; border-radius: 8px;}" +
                ".logo-container {text-align: center; margin-bottom: 1rem;}" +
                ".logo-container img {width: 7rem; height: 2.5rem; object-fit: cover; border-radius: 0.5rem;}" +
                "h2 {color: #00aaff; text-align: center;}" +
                "p {font-size: 1rem; color: #cccccc;}" +
                "table {width: 100%; border-collapse: collapse; margin: 1rem 0;}" +
                "th, td {padding: 1rem; border: 0.1rem solid #444444; text-align: left;}" +
                "th {background-color: #333333; color: #00aaff;}" +
                "td {background-color: #1e1e1e; color: #cccccc;}" +
                "a {color: #00aaff; text-decoration: none;}" +
                "a:hover {text-decoration: underline;}" +
                "@media only screen and (max-width: 600px) {" +
                ".email-container {padding: 0.5rem; margin: 2rem auto;}" +
                "table {font-size: 0.9rem;}" +
                "th, td {padding: 0.8rem;}" +
                ".logo-container img {width: 6rem; height: 6rem;}" +
                "h2 {font-size: 1.5rem;}" +
                "p {font-size: 0.9rem;}" +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='email-container'>" +
                "<div class='logo-container'>" +
                "<img src="+"https://tridentpublicdata.s3.ap-south-1.amazonaws.com/logos/tat-logo.jpg"+" alt='Logo'>" +
                "</div>" +
                "<h2>Payment Receipt</h2>" +
                "<p>Dear " + name + ",</p>" +
                "<p>Your previous money receipt with Money Receipt No : " + oldMrNo + " has been cancelled. Below are the details of your transaction processed with new money receipt No :</p>" +
                "<table>" +
                "<tr><th>Details</th><th>Information</th></tr>" +
                "<tr><td>MR Number</td><td>" + mrNumber + "</td></tr>" +
                "<tr><td>Name</td><td>" + name + "</td></tr>" +
                "<tr><td>Amount Paid</td><td>₹" + amountPaid + "</td></tr>" +
                "</table>" +
                "<p>Your payment receipt is attached for your reference.</p>" +
                "<p>If you have any questions or concerns, feel free to contact our support team at <a href='tel:" + supportPhoneNumber + "'>" + supportPhoneNumber + "</a>.</p>" +
                "<p>Best Regards,<br>Accounts Department<br>Trident Group of Institutions</p>" +
                "</div>" +
                "</body>" +
                "</html>";


        // Set the email content
//        helper.setText(emailContent, true);
        microsoftGraphService.sendEmailWithAttachment("elitecracker25@gmail.com",mrNumber + " : Trident Payment Receipt" ,emailContent, receiptPdfBytes, graphToken);
//
//        // Attach the PDF receipt
//        helper.addAttachment("Payment_Receipt.pdf", new ByteArrayResource(receiptPdfBytes));
//
//        // Send the email
//        mailSender.send(message);

        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> sendPaymentReceiptEmail(
            String recipientEmail, Long mrNumber, String name, BigDecimal amountPaid, String supportPhoneNumber, PDFObject pdfObject, String graphToken)
            throws MessagingException, IOException {
//        JwtAuthenticationToken authentication = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//        Jwt jwt = authentication.getToken();
//        Jwt jwts = (Jwt)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//        Map<String,Object> claims = jwts.getClaims();
        byte[] receiptPdfBytes = pdfGenerationService.generatePdf(pdfObject);

        logger.info("Generated receipt pdf bytes");
        logger.info("Generated PDF : {}", LocalTime.now());
        // Create a MIME message
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//        // Set sender and recipient
//        helper.setFrom("mohantyswayam2001@gmail.com");
//        helper.setTo("elitecracker25@gmail.com");
//        helper.setSubject("Payment Receipt");

        // Email HTML content with dynamic data
        String emailContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>Payment Receipt</title>" +
                "<style>" +
                "body {font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #121212; color: #ffffff;}" +
                ".email-container {max-width: 35rem; margin: 5rem auto; background-color: #1e1e1e; border: 0.3rem solid #333333; padding: 1rem; border-radius: 8px;}" +
                ".logo-container {text-align: center; margin-bottom: 1rem;}" +
                ".logo-container img {width: 7rem; height: 2.5rem; object-fit: cover; border-radius: 0.5rem;}" +
                "h2 {color: #00aaff; text-align: center;}" +
                "p {font-size: 1rem; color: #cccccc;}" +
                "table {width: 100%; border-collapse: collapse; margin: 1rem 0;}" +
                "th, td {padding: 1rem; border: 0.1rem solid #444444; text-align: left;}" +
                "th {background-color: #333333; color: #00aaff;}" +
                "td {background-color: #1e1e1e; color: #cccccc;}" +
                "a {color: #00aaff; text-decoration: none;}" +
                "a:hover {text-decoration: underline;}" +
                "@media only screen and (max-width: 600px) {" +
                ".email-container {padding: 0.5rem; margin: 2rem auto;}" +
                "table {font-size: 0.9rem;}" +
                "th, td {padding: 0.8rem;}" +
                ".logo-container img {width: 6rem; height: 6rem;}" +
                "h2 {font-size: 1.5rem;}" +
                "p {font-size: 0.9rem;}" +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='email-container'>" +
                "<div class='logo-container'>" +
                "<img src="+"https://tridentpublicdata.s3.ap-south-1.amazonaws.com/logos/tat-logo.jpg"+" alt='Logo'>" +
                "</div>" +
                "<h2>Payment Receipt</h2>" +
                "<p>Dear " + name + ",</p>" +
                "<p>Thank you for your fee payment. Below are the details of your transaction:</p>" +
                "<table>" +
                "<tr><th>Details</th><th>Information</th></tr>" +
                "<tr><td>MR Number</td><td>" + mrNumber + "</td></tr>" +
                "<tr><td>Name</td><td>" + name + "</td></tr>" +
                "<tr><td>Amount Paid</td><td>₹" + amountPaid + "</td></tr>" +
                "</table>" +
                "<p>Your payment receipt is attached for your reference.</p>" +
                "<p>If you have any questions or concerns, feel free to contact our support team at <a href='tel:" + supportPhoneNumber + "'>" + supportPhoneNumber + "</a>.</p>" +
                "<p>Best Regards,<br>Accounts Department<br>Trident Group of Institutions</p>" +
                "</div>" +
                "</body>" +
                "</html>";


        // Set the email content
//        helper.setText(emailContent, true);
        microsoftGraphService.sendEmailWithAttachment("elitecracker25@gmail.com",mrNumber + " : Trident Payment Receipt" ,emailContent, receiptPdfBytes, graphToken);
//
//        // Attach the PDF receipt
//        helper.addAttachment("Payment_Receipt.pdf", new ByteArrayResource(receiptPdfBytes));
//
//        // Send the email
//        mailSender.send(message);

        return CompletableFuture.completedFuture(null);
    }
}
