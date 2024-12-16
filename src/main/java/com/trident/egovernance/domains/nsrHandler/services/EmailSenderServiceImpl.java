package com.trident.egovernance.domains.nsrHandler.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailSenderServiceImpl {
    private final JavaMailSender mailSender;

    public EmailSenderServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTridentCredentialsEmail(String microsoftMail, String password) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String inductionStart = "21st October 2024";
        String inductionTimeStart = "11AM";
        String inductionTimeEnd = "1PM";
        String inductionEnd = "25th October 2024";
        String classStart = "1st November 2024";
        // Sender and Recipient
        helper.setFrom("mohantyswayam2001@gmail.com");
        helper.setTo("surajsahoo2606@gmail.com");
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
    }
}
