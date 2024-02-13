package com.example.epamProject.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private  DoctorService doctorService;


    @Async
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    public void sendEmailToDoctor(Long doctorId, String subject, String message) {
        // Retrieve doctor's email address based on doctorId
        String doctorEmail = doctorService.getDoctorEmailById(doctorId);
        String email = (String) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        // Create MimeMessage
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            // Set sender, recipient, subject, and text of the email
            helper.setFrom(email);
            helper.setTo(doctorEmail);
            helper.setSubject(subject);
            helper.setText(message);

            // Send the email
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
    public void sendApprovalEmail(String recipientEmail, String medicineName) {
        String subject = "Approval for Medicine Purchase";
        String message = "Dear User,\n\nYour receipt for the medicine '" + medicineName + "' has been approved. You can now place your order.\n\nBest regards,\nThe Admin";
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        String email = (String) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        try {
            // Set sender, recipient, subject, and text of the email
           // helper.setFrom(email);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(message);

            // Send the email
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }

    }

    public void sendRejectionEmail(String recipientEmail, String medicineName) {
        String subject = "Rejection of Medicine Purchase";
        String message = "Dear User,\n\nWe regret to inform you that your receipt for the medicine '" + medicineName + "' has been rejected. Please contact our support team for further assistance.\n\nBest regards,\nThe Admin";
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        String email = (String) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        try {
            // Set sender, recipient, subject, and text of the email
            //helper.setFrom(email);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(message);

            // Send the email
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}
