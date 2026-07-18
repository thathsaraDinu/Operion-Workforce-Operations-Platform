package com.dinoryn.operion.service.impl;

import com.dinoryn.operion.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        // TODO: Implement actual email sending using JavaMailSender or a service like SendGrid
        // For development, log the reset link
        String resetLink = "http://localhost:5173/reset-password?token=" + resetToken;
        
        log.info("========================================");
        log.info("PASSWORD RESET EMAIL");
        log.info("To: {}", to);
        log.info("Reset Link: {}", resetLink);
        log.info("========================================");
        
        // In production, this would send an actual email:
        // SimpleMailMessage message = new SimpleMailMessage();
        // message.setTo(to);
        // message.setSubject("Password Reset Request");
        // message.setText("Click the link to reset your password: " + resetLink);
        // mailSender.send(message);
    }

    @Override
    public void sendPasswordChangedNotification(String to) {
        // TODO: Implement actual email sending using JavaMailSender or a service like SendGrid
        // For development, log the notification
        log.info("========================================");
        log.info("PASSWORD CHANGED NOTIFICATION");
        log.info("To: {}", to);
        log.info("Message: Your password has been changed successfully.");
        log.info("If you did not make this change, please contact support immediately.");
        log.info("========================================");
        
        // In production, this would send an actual email:
        // SimpleMailMessage message = new SimpleMailMessage();
        // message.setTo(to);
        // message.setSubject("Password Changed Notification");
        // message.setText("Your password has been changed successfully. If you did not make this change, please contact support immediately.");
        // mailSender.send(message);
    }
}
