package com.dinoryn.operion.service;

public interface EmailService {

    void sendPasswordResetEmail(String to, String resetToken);

    void sendPasswordChangedNotification(String to);

}
