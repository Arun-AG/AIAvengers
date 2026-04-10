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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ExceptionAlertConfig alertConfig;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private EmailRequest emailRequest;
    private String sampleStackTrace;

    @BeforeEach
    void setUp() {
        emailRequest = new EmailRequest();
        emailRequest.setServiceName("User Service");
        emailRequest.setServer("prod-server-01");
        emailRequest.setTime("2025-01-15 10:30:45");
        emailRequest.setSeverity("HIGH");
        emailRequest.setRequestMethod("POST");
        emailRequest.setRequestURI("/api/users/create");
        emailRequest.setExceptionName("NullPointerException");
        emailRequest.setInputRequest("{\"name\":\"John Doe\",\"email\":\"john@example.com\"}");

        sampleStackTrace = "java.lang.NullPointerException: Cannot invoke \"String.length()\" because \"str\" is null\n" +
                "\tat com.example.UserService.processUser(UserService.java:45)\n" +
                "\tat com.example.UserService.createUser(UserService.java:32)\n" +
                "\tat com.example.UserController.createUser(UserController.java:28)\n" +
                "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\n" +
                "\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
                "\tat java.base/java.lang.reflect.Method.invoke(Method.java:568)\n" +
                "\tat org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:205)\n" +
                "\tat org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:150)\n" +
                "\tat org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:117)\n" +
                "\tat org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:895)\n" +
                "\tat org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:808)\n" +
                "\tat org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\n" +
                "\tat org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1071)\n" +
                "\tat org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:964)\n" +
                "\tat org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1006)\n" +
                "\tat org.springframework.web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:909)\n" +
                "\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:555)\n" +
                "\tat org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:883)\n" +
                "\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:623)\n" +
                "\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:209)\n" +
                "\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:156)\n" +
                "\tat org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)";

        emailRequest.setBodyContent(sampleStackTrace);

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(alertConfig.getSenderEmail()).thenReturn("alerts@company.com");
        when(alertConfig.getRecipientEmails()).thenReturn(Arrays.asList("admin@company.com", "dev-team@company.com"));
        when(alertConfig.getEmailSubject()).thenReturn("🚨 Exception Alert - User Service");
    }

    @Test
    void testSendExceptionAlert_Success() {
        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(mimeMessage);
        verify(alertConfig, times(1)).getSenderEmail();
        verify(alertConfig, times(1)).getRecipientEmails();
        verify(alertConfig, times(1)).getEmailSubject();
    }

    @Test
    void testSendExceptionAlert_WithNullInputRequest() {
        emailRequest.setInputRequest(null);
        
        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendExceptionAlert_WithEmptyInputRequest() {
        emailRequest.setInputRequest("   ");
        
        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendExceptionAlert_WithEmptyStackTrace() {
        emailRequest.setBodyContent("");
        
        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendExceptionAlert_MailSendingFailure() {
        doThrow(new RuntimeException("Mail server unavailable")).when(mailSender).send(any(MimeMessage.class));
        
        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendExceptionAlert_WithComplexStackTrace() {
        String complexStackTrace = "org.springframework.dao.DataIntegrityViolationException: could not execute statement; SQL [n/a]; constraint [users_email_key]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement\n" +
                "\tat org.springframework.orm.jpa.vendor.HibernateJpaDialect.convertHibernateAccessException(HibernateJpaDialect.java:169)\n" +
                "\tat org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:138)\n" +
                "\tat org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.translateExceptionIfPossible(AbstractEntityManagerFactoryBean.java:535)\n" +
                "\tat org.springframework.dao.support.ChainedPersistenceExceptionTranslator.translateExceptionIfPossible(ChainedPersistenceExceptionTranslator.java:61)\n" +
                "\tat org.springframework.dao.support.DataAccessUtils.translateIfNecessary(DataAccessUtils.java:243)\n" +
                "\tat org.springframework.dao.support.PersistenceExceptionTranslationInterceptor.invoke(PersistenceExceptionTranslationInterceptor.java:152)\n" +
                "\tat org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:186)\n" +
                "\tat org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.proceed(CglibAopProxy.java:763)\n" +
                "\tat org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:708)\n" +
                "\tat com.example.UserRepository$$EnhancerBySpringCGLIB$$1a2b3c4d.save(<generated>)\n" +
                "\tat com.example.UserService.createUser(UserService.java:42)\n" +
                "\tat com.example.UserController.createUser(UserController.java:35)\n" +
                "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\n" +
                "\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
                "\tat java.base/java.lang.reflect.Method.invoke(Method.java:568)";

        emailRequest.setExceptionName("DataIntegrityViolationException");
        emailRequest.setBodyContent(complexStackTrace);
        emailRequest.setSeverity("CRITICAL");
        
        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendExceptionAlert_WithSpecialCharactersInInput() {
        String specialInput = "{\"name\":\"John O'Connor\",\"email\":\"john+test@example.com\",\"description\":\"This is a <test> with & special \\\"characters\\\"\"}";
        emailRequest.setInputRequest(specialInput);
        
        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void testSendExceptionAlert_WithMultipleRecipients() {
        List<String> recipients = Arrays.asList(
            "admin@company.com", 
            "dev-team@company.com", 
            "ops-team@company.com",
            "manager@company.com"
        );
        when(alertConfig.getRecipientEmails()).thenReturn(recipients);
        
        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).send(mimeMessage);
        verify(alertConfig, times(1)).getRecipientEmails();
    }

    @Test
    void testSendExceptionAlert_WithLongStackTrace() {
        StringBuilder longStackTrace = new StringBuilder();
        longStackTrace.append("java.lang.StackOverflowError\n");
        for (int i = 0; i < 100; i++) {
            longStackTrace.append("\tat com.example.deep.nested.Method").append(i).append(".call(Method").append(i).append(".java:").append(i).append(")\n");
        }
        
        emailRequest.setExceptionName("StackOverflowError");
        emailRequest.setBodyContent(longStackTrace.toString());
        emailRequest.setSeverity("FATAL");
        
        emailService.sendExceptionAlert(emailRequest);

        verify(mailSender, times(1)).send(mimeMessage);
    }
}
