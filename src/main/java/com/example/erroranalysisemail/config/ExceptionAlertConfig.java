package com.example.erroranalysisemail.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class ExceptionAlertConfig {

    @Value("${spring.mail.host}")
    private String smtpServer;

    @Value("${spring.mail.port}")
    private int smtpPort;

    @Value("${spring.mail.username}")
    private String smtpUsername;

    @Value("${spring.mail.password}")
    private String smtpPassword;

    @Value("${exception.alert.recipient}")
    private String recipientEmails;

    @Value("${exception.alert.sender}")
    private String senderEmail;

    @Value("${exception.alert.subject}")
    private String emailSubject;

    // Getters
    public String getSmtpServer() {
        return smtpServer;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public String getSmtpUsername() {
        return smtpUsername;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public List<String> getRecipientEmails() {
        return Arrays.asList(recipientEmails.split(","));
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    @Override
    public String toString() {
        return "ExceptionAlertConfig{" +
                "smtpServer='" + smtpServer + '\'' +
                ", smtpPort=" + smtpPort +
                ", smtpUsername='" + smtpUsername + '\'' +
                ", recipientEmails='" + recipientEmails + '\'' +
                ", senderEmail='" + senderEmail + '\'' +
                ", emailSubject='" + emailSubject + '\'' +
                '}';
    }
}
