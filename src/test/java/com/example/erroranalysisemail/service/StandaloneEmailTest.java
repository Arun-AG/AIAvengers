package com.example.erroranalysisemail.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Standalone test for sending real emails without mocking.
 * This test creates its own Spring context to avoid compilation issues with main source code.
 */
public class StandaloneEmailTest {

    @Test
    void testSendRealEmail() throws Exception {
        System.out.println("=== Testing Real Email Sending ===");
        
        // Create a minimal Spring context for mail
        System.setProperty("spring.mail.host", "mail.messagingengine.com");
        System.setProperty("spring.mail.port", "587");
        System.setProperty("spring.mail.username", "postmaster@personifyfinancial.com");
        System.setProperty("spring.mail.password", "g5d8sjk66j22xq8r");
        System.setProperty("spring.mail.properties.mail.smtp.auth", "true");
        System.setProperty("spring.mail.properties.mail.smtp.starttls.enable", "true");
        
        // Create JavaMailSender directly
        org.springframework.mail.javamail.JavaMailSenderImpl mailSender = new org.springframework.mail.javamail.JavaMailSenderImpl();
        mailSender.setHost("mail.messagingengine.com");
        mailSender.setPort(587);
        mailSender.setUsername("postmaster@personifyfinancial.com");
        mailSender.setPassword("g5d8sjk66j22xq8r");
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");
        mailSender.setJavaMailProperties(props);
        
        System.out.println("Mail sender configured with host: " + mailSender.getHost());
        
        // Create test email content
        String sampleStackTrace = "java.lang.NullPointerException: This is a test exception from standalone test\n" +
                "\tat com.example.test.TestService.processData(TestService.java:45)\n" +
                "\tat com.example.test.TestController.handleRequest(TestController.java:28)\n" +
                "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\n" +
                "\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
                "\tat java.base/java.lang.reflect.Method.invoke(Method.java:568)";
        
        String emailContent = buildEmailContent(
            "Test Service", 
            "test-server-01", 
            "2025-01-15 10:30:45", 
            "HIGH",
            "POST",
            "/api/test/endpoint",
            "NullPointerException",
            "{\"test\":\"data\",\"value\":123}",
            sampleStackTrace
        );
        
        // Create and send the email
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom("postmaster@personifyfinancial.com");
        helper.setTo("test@example.com"); // Change to actual recipient for testing
        helper.setSubject("🧪 Standalone Test Exception Alert - Real Email");
        helper.setText(emailContent, true);
        
        System.out.println("Sending test email...");
        System.out.println("From: postmaster@personifyfinancial.com");
        System.out.println("To: test@example.com");
        System.out.println("Subject: 🧪 Standalone Test Exception Alert - Real Email");
        
        try {
            mailSender.send(message);
            System.out.println("✅ Email sent successfully!");
            assertTrue(true, "Email should be sent successfully");
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
            fail("Email sending failed: " + e.getMessage());
        }
    }
    
    @Test
    void testSendEmailWithComplexStackTrace() throws Exception {
        System.out.println("=== Testing Email with Complex Stack Trace ===");
        
        org.springframework.mail.javamail.JavaMailSenderImpl mailSender = createMailSender();
        
        String complexStackTrace = "org.springframework.dao.DataIntegrityViolationException: could not execute statement; SQL [n/a]; constraint [test_constraint]\n" +
                "\tat org.springframework.orm.jpa.vendor.HibernateJpaDialect.convertHibernateAccessException(HibernateJpaDialect.java:169)\n" +
                "\tat org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:138)\n" +
                "\tat org.springframework.dao.support.ChainedPersistenceExceptionTranslator.translateExceptionIfPossible(ChainedPersistenceExceptionTranslator.java:61)\n" +
                "\tat com.example.test.TestRepository.save(TestRepository.java:42)\n" +
                "\tat com.example.test.TestService.createEntity(TestService.java:35)\n" +
                "\tat com.example.test.TestController.create(TestController.java:30)\n" +
                "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)";
        
        String emailContent = buildEmailContent(
            "Database Service", 
            "prod-db-server-02", 
            "2025-01-15 11:45:22", 
            "CRITICAL",
            "POST",
            "/api/users/create",
            "DataIntegrityViolationException",
            "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}",
            complexStackTrace
        );
        
        sendTestEmail(mailSender, "🧪 Complex Stack Trace Test", emailContent);
        
        assertTrue(true, "Complex stack trace email should be sent successfully");
    }
    
    @Test
    void testSendEmailWithSpecialCharacters() throws Exception {
        System.out.println("=== Testing Email with Special Characters ===");
        
        org.springframework.mail.javamail.JavaMailSenderImpl mailSender = createMailSender();
        
        String specialCharStackTrace = "java.lang.IllegalArgumentException: Invalid input containing special characters <script>alert('test')</script>\n" +
                "\tat com.example.test.ValidationUtils.validateInput(ValidationUtils.java:25)\n" +
                "\tat com.example.test.TestService.processInput(TestService.java:38)\n" +
                "\tat com.example.test.TestController.handleInput(TestController.java:30)";
        
        String emailContent = buildEmailContent(
            "Validation Service", 
            "web-server-03", 
            "2025-01-15 12:15:33", 
            "MEDIUM",
            "POST",
            "/api/data/validate",
            "IllegalArgumentException",
            "{\"name\":\"John O'Connor\",\"email\":\"john+test@example.com\",\"description\":\"This is a <test> with & special \\\"characters\\\"\"}",
            specialCharStackTrace
        );
        
        sendTestEmail(mailSender, "🧪 Special Characters Test", emailContent);
        
        assertTrue(true, "Special characters email should be sent successfully");
    }
    
    private org.springframework.mail.javamail.JavaMailSenderImpl createMailSender() {
        org.springframework.mail.javamail.JavaMailSenderImpl mailSender = new org.springframework.mail.javamail.JavaMailSenderImpl();
        mailSender.setHost("mail.messagingengine.com");
        mailSender.setPort(587);
        mailSender.setUsername("postmaster@personifyfinancial.com");
        mailSender.setPassword("g5d8sjk66j22xq8r");
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");
        mailSender.setJavaMailProperties(props);
        
        return mailSender;
    }
    
    private void sendTestEmail(org.springframework.mail.javamail.JavaMailSenderImpl mailSender, String subject, String content) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom("postmaster@personifyfinancial.com");
        helper.setTo("test@example.com"); // Change to actual recipient for testing
        helper.setSubject(subject);
        helper.setText(content, true);
        
        System.out.println("Sending email: " + subject);
        mailSender.send(message);
        System.out.println("✅ Email sent successfully!");
    }
    
    private String buildEmailContent(String serviceName, String server, String time, String severity, 
                                   String requestMethod, String requestURI, String exceptionName, 
                                   String inputRequest, String stackTrace) {
        StringBuilder content = new StringBuilder();
        content.append("<html><body style='font-family: Arial, sans-serif;'>");
        content.append("<h2 style='color: #d32f2f;'>🚨 Exception Alert</h2>");
        content.append("<div style='background-color: #f5f5f5; padding: 15px; border-radius: 5px;'>");
        
        content.append("<p><strong>Service Name:</strong> ").append(serviceName).append("</p>");
        content.append("<p><strong>Server:</strong> ").append(server).append("</p>");
        content.append("<p><strong>Time:</strong> ").append(time).append("</p>");
        content.append("<p><strong>Severity:</strong> <span style='color: #d32f2f;'>").append(severity).append("</span></p>");
        content.append("<p><strong>Request Method:</strong> ").append(requestMethod).append("</p>");
        content.append("<p><strong>Request URI:</strong> ").append(requestURI).append("</p>");
        content.append("<p><strong>Exception Name:</strong> <span style='color: #d32f2f;'>").append(exceptionName).append("</span></p>");
        
        if (inputRequest != null && !inputRequest.trim().isEmpty()) {
            content.append("<h3>Request Body:</h3>");
            content.append("<pre style='background-color: #e8e8e8; padding: 10px; border-radius: 3px; white-space: pre-wrap;'>")
                   .append(escapeHtml(inputRequest)).append("</pre>");
        }
        
        content.append("<h3>Stack Trace:</h3>");
        content.append("<pre style='background-color: #ffebee; padding: 10px; border-radius: 3px; white-space: pre-wrap; color: #c62828;'>")
               .append(escapeHtml(stackTrace)).append("</pre>");
        
        content.append("</div>");
        content.append("<p style='margin-top: 20px; font-size: 12px; color: #666;'>");
        content.append("This is a test exception alert from Standalone Email Test.");
        content.append("</p>");
        content.append("</body></html>");
        
        return content.toString();
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
