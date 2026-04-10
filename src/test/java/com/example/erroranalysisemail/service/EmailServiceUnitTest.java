package com.example.erroranalysisemail.service;

import com.example.erroranalysisemail.config.ExceptionAlertConfig;
import com.example.erroranalysisemail.model.EmailRequest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceUnitTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ExceptionAlertConfig alertConfig;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private EmailRequest emailRequest;

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(alertConfig.getSenderEmail()).thenReturn("alerts@company.com");
        when(alertConfig.getRecipientEmails()).thenReturn(Arrays.asList("admin@company.com", "dev-team@company.com"));
        when(alertConfig.getEmailSubject()).thenReturn("🚨 Exception Alert");
    }

    @Test
    void testSendExceptionAlert_WithNullPointerException() {
        emailRequest = createEmailRequest(
            "NullPointerException",
            "java.lang.NullPointerException: Cannot invoke \"String.length()\" because \"str\" is null\n" +
            "\tat com.example.UserService.processUser(UserService.java:45)\n" +
            "\tat com.example.UserService.createUser(UserService.java:32)\n" +
            "\tat com.example.UserController.createUser(UserController.java:28)\n" +
            "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
            "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\n" +
            "\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
            "\tat java.base/java.lang.reflect.Method.invoke(Method.java:568)"
        );

        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendExceptionAlert_WithDatabaseException() {
        emailRequest = createEmailRequest(
            "DataIntegrityViolationException",
            "org.springframework.dao.DataIntegrityViolationException: could not execute statement; SQL [n/a]; constraint [users_email_key]\n" +
            "\tat org.springframework.orm.jpa.vendor.HibernateJpaDialect.convertHibernateAccessException(HibernateJpaDialect.java:169)\n" +
            "\tat org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:138)\n" +
            "\tat org.springframework.dao.support.ChainedPersistenceExceptionTranslator.translateExceptionIfPossible(ChainedPersistenceExceptionTranslator.java:61)\n" +
            "\tat com.example.UserRepository$$EnhancerBySpringCGLIB$$1a2b3c4d.save(<generated>)\n" +
            "\tat com.example.UserService.createUser(UserService.java:42)"
        );

        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendExceptionAlert_WithComplexStackOverflow() {
        StringBuilder stackTrace = new StringBuilder();
        stackTrace.append("java.lang.StackOverflowError\n");
        for (int i = 0; i < 50; i++) {
            stackTrace.append("\tat com.example.deep.nested.Method").append(i)
                     .append(".call(Method").append(i).append(".java:").append(i).append(")\n");
        }

        emailRequest = createEmailRequest("StackOverflowError", stackTrace.toString());
        emailRequest.setSeverity("FATAL");

        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendExceptionAlert_WithSpecialCharacters() {
        emailRequest = createEmailRequest(
            "IllegalArgumentException",
            "java.lang.IllegalArgumentException: Invalid input parameter: name contains special chars <script>alert('xss')</script>\n" +
            "\tat com.example.ValidationUtils.validateInput(ValidationUtils.java:25)\n" +
            "\tat com.example.UserService.processUser(UserService.java:38)\n" +
            "\tat com.example.UserController.createUser(UserController.java:30)"
        );
        emailRequest.setInputRequest("{\"name\":\"John O'Connor\",\"email\":\"john+test@example.com\",\"description\":\"This is a <test> with & special \\\"characters\\\"\"}");

        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendExceptionAlert_WithNullInputRequest() {
        emailRequest = createEmailRequest(
            "RuntimeException",
            "java.lang.RuntimeException: Unexpected error occurred\n" +
            "\tat com.example.Service.process(Service.java:100)"
        );
        emailRequest.setInputRequest(null);

        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendExceptionAlert_WithEmptyStackTrace() {
        emailRequest = createEmailRequest("CustomException", "");
        emailRequest.setSeverity("LOW");

        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendExceptionAlert_WithMailSendingFailure() {
        emailRequest = createEmailRequest(
            "IOException",
            "java.io.IOException: Connection refused\n" +
            "\tat java.net.Socket.connect(Socket.java:593)\n" +
            "\tat java.net.Socket.connect(Socket.java:540)"
        );

        doThrow(new RuntimeException("Mail server unavailable")).when(mailSender).send(any(MimeMessage.class));

        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendExceptionAlert_WithMultipleRecipients() {
        when(alertConfig.getRecipientEmails()).thenReturn(Arrays.asList(
            "admin@company.com", 
            "dev-team@company.com", 
            "ops-team@company.com",
            "manager@company.com"
        ));

        emailRequest = createEmailRequest(
            "SecurityException",
            "java.lang.SecurityException: Access denied\n" +
            "\tat java.security.AccessController.checkPermission(AccessController.java:455)"
        );

        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).send(mimeMessage);
        verify(alertConfig, times(1)).getRecipientEmails();
    }

    private EmailRequest createEmailRequest(String exceptionName, String stackTrace) {
        EmailRequest request = new EmailRequest();
        request.setServiceName("User Service");
        request.setServer("prod-server-01");
        request.setTime("2025-01-15 10:30:45");
        request.setSeverity("HIGH");
        request.setRequestMethod("POST");
        request.setRequestURI("/api/users/create");
        request.setExceptionName(exceptionName);
        request.setBodyContent(stackTrace);
        request.setInputRequest("{\"name\":\"John Doe\",\"email\":\"john@example.com\"}");
        return request;
    }
}
