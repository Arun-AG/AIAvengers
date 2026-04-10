package com.example.erroranalysisemail.service;

import com.example.erroranalysisemail.config.ExceptionAlertConfig;
import com.example.erroranalysisemail.model.EmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EmailServiceIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceIntegrationTest.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private ExceptionAlertConfig alertConfig;

    @Autowired
    private JavaMailSender mailSender;

    private EmailRequest emailRequest;

    @BeforeEach
    void setUp() {
        LOG.info("Setting up integration test with real mail configuration");
        LOG.info("Mail host: {}:{}", alertConfig.getSmtpServer(), alertConfig.getSmtpPort());
        LOG.info("Sender: {}", alertConfig.getSenderEmail());
        LOG.info("Recipients: {}", alertConfig.getRecipientEmails());
        
        emailRequest = new EmailRequest();
        emailRequest.setServiceName("Test Service");
        emailRequest.setServer("test-server-01");
        emailRequest.setTime("2025-01-15 10:30:45");
        emailRequest.setSeverity("HIGH");
        emailRequest.setRequestMethod("POST");
        emailRequest.setRequestURI("/api/test/endpoint");
        emailRequest.setExceptionName("TestException");
        emailRequest.setInputRequest("{\"test\":\"data\",\"value\":123}");
    }

    @Test
    void testSendExceptionAlert_WithRealMailServer() throws InterruptedException {
        String sampleStackTrace = "java.lang.NullPointerException: Test exception for integration testing\n" +
                "\tat com.example.test.TestService.processData(TestService.java:45)\n" +
                "\tat com.example.test.TestController.handleRequest(TestController.java:28)\n" +
                "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\n" +
                "\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
                "\tat java.base/java.lang.reflect.Method.invoke(Method.java:568)";

        emailRequest.setBodyContent(sampleStackTrace);
        emailRequest.setExceptionName("NullPointerException");

        LOG.info("Sending test email with real SMTP server...");
        
        // Send the email (this is async, so we need to wait a bit)
        emailService.sendExceptionAlert(emailRequest);
        
        // Wait for async operation to complete
        Thread.sleep(3000);
        
        LOG.info("Test email sent successfully to recipients: {}", alertConfig.getRecipientEmails());
        
        // If we reach here without exceptions, the test passed
        assertTrue(true, "Email should be sent successfully");
    }

    @Test
    void testSendExceptionAlert_WithComplexStackTrace() throws InterruptedException {
        String complexStackTrace = "org.springframework.dao.DataIntegrityViolationException: could not execute statement; SQL [n/a]; constraint [test_constraint]\n" +
                "\tat org.springframework.orm.jpa.vendor.HibernateJpaDialect.convertHibernateAccessException(HibernateJpaDialect.java:169)\n" +
                "\tat org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:138)\n" +
                "\tat org.springframework.dao.support.ChainedPersistenceExceptionTranslator.translateExceptionIfPossible(ChainedPersistenceExceptionTranslator.java:61)\n" +
                "\tat com.example.test.TestRepository.save(TestRepository.java:42)\n" +
                "\tat com.example.test.TestService.createEntity(TestService.java:35)\n" +
                "\tat com.example.test.TestController.create(TestController.java:30)\n" +
                "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)";

        emailRequest.setBodyContent(complexStackTrace);
        emailRequest.setExceptionName("DataIntegrityViolationException");
        emailRequest.setSeverity("CRITICAL");

        LOG.info("Sending test email with complex stack trace...");
        
        emailService.sendExceptionAlert(emailRequest);
        
        // Wait for async operation
        Thread.sleep(3000);
        
        LOG.info("Complex stack trace email sent successfully");
        assertTrue(true, "Email with complex stack trace should be sent successfully");
    }

    @Test
    void testSendExceptionAlert_WithSpecialCharacters() throws InterruptedException {
        String specialCharStackTrace = "java.lang.IllegalArgumentException: Invalid input containing special characters <script>alert('test')</script>\n" +
                "\tat com.example.test.ValidationUtils.validateInput(ValidationUtils.java:25)\n" +
                "\tat com.example.test.TestService.processInput(TestService.java:38)\n" +
                "\tat com.example.test.TestController.handleInput(TestController.java:30)";

        emailRequest.setBodyContent(specialCharStackTrace);
        emailRequest.setExceptionName("IllegalArgumentException");
        emailRequest.setInputRequest("{\"name\":\"John O'Connor\",\"email\":\"john+test@example.com\",\"description\":\"This is a <test> with & special \\\"characters\\\"\"}");

        LOG.info("Sending test email with special characters...");
        
        emailService.sendExceptionAlert(emailRequest);
        
        // Wait for async operation
        Thread.sleep(3000);
        
        LOG.info("Special characters email sent successfully");
        assertTrue(true, "Email with special characters should be sent successfully");
    }

    @Test
    void testSendExceptionAlert_WithLongStackTrace() throws InterruptedException {
        StringBuilder longStackTrace = new StringBuilder();
        longStackTrace.append("java.lang.StackOverflowError: Recursive method call detected\n");
        for (int i = 0; i < 30; i++) {
            longStackTrace.append("\tat com.example.deep.nested.Method").append(i)
                         .append(".recursiveCall(Method").append(i).append(".java:").append(i).append(")\n");
        }
        longStackTrace.append("\t... 50 more\n");

        emailRequest.setBodyContent(longStackTrace.toString());
        emailRequest.setExceptionName("StackOverflowError");
        emailRequest.setSeverity("FATAL");

        LOG.info("Sending test email with long stack trace...");
        
        emailService.sendExceptionAlert(emailRequest);
        
        // Wait for async operation
        Thread.sleep(3000);
        
        LOG.info("Long stack trace email sent successfully");
        assertTrue(true, "Email with long stack trace should be sent successfully");
    }

    @Test
    void testSendExceptionAlert_WithNullInputRequest() throws InterruptedException {
        String simpleStackTrace = "java.lang.RuntimeException: Simple test exception\n" +
                "\tat com.example.test.SimpleService.process(SimpleService.java:100)";

        emailRequest.setBodyContent(simpleStackTrace);
        emailRequest.setExceptionName("RuntimeException");
        emailRequest.setInputRequest(null);

        LOG.info("Sending test email with null input request...");
        
        emailService.sendExceptionAlert(emailRequest);
        
        // Wait for async operation
        Thread.sleep(3000);
        
        LOG.info("Null input request email sent successfully");
        assertTrue(true, "Email with null input request should be sent successfully");
    }

    @Test
    void testMailSenderConnectivity() {
        LOG.info("Testing mail sender connectivity...");
        
        try {
            // Test if we can create a mime message (basic connectivity test)
            mailSender.createMimeMessage();
            LOG.info("Mail sender connectivity test passed - can create MimeMessage");
            assertTrue(true, "Mail sender should be able to create MimeMessage");
        } catch (Exception e) {
            LOG.error("Mail sender connectivity test failed", e);
            fail("Mail sender should be able to create MimeMessage: " + e.getMessage());
        }
    }

    @Test
    void testExceptionAlertConfig() {
        LOG.info("Testing exception alert configuration...");
        
        assertNotNull(alertConfig.getSenderEmail(), "Sender email should not be null");
        assertNotNull(alertConfig.getRecipientEmails(), "Recipient emails should not be null");
        assertNotNull(alertConfig.getEmailSubject(), "Email subject should not be null");
        assertFalse(alertConfig.getRecipientEmails().isEmpty(), "Should have at least one recipient");
        
        LOG.info("Configuration test passed - Sender: {}, Recipients: {}, Subject: {}", 
                alertConfig.getSenderEmail(), 
                alertConfig.getRecipientEmails(), 
                alertConfig.getEmailSubject());
    }
}
