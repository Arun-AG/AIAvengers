package com.example.erroranalysisemail.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
public class ExceptionalControllerAdvice {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionalControllerAdvice.class);
    private static final String EXCEPTION_LOG_MESSAGE = "Exception occurred: {} - {}";
    private static final String EXCEPTION_MAIL_SENT_LOG = "Exception notification email sent successfully";
    private static final String SEVERITY = "HIGH";
    private static final String SERVICE_NAME_DE = "ErrorAnalysisEmail";

    @Autowired
    private Utils utils;

    @Autowired
    private ExceptionControllerUtils exceptionControllerUtils;

    @Autowired
    private ControllerHelper controllerHelper;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(HttpServletRequest req, Exception e) {
        LOG.info(EXCEPTION_LOG_MESSAGE, e.getClass(), e);
        try {
            LOG.info(EXCEPTION_MAIL_SENT_LOG);
            buildEmailRequestAndSendMail(req, e);
        } catch (Exception e1) {
            LOG.error("Failed to send exception notification email", e1);
        }
        String responseJson = setServiceStatusForExceptionAlert(999, "Internal Server Error. Please try again");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseJson);
    }

    public void buildEmailRequestAndSendMail(HttpServletRequest req, Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        EmailRequest request = new EmailRequest();
        String serverName = utils.getServerName();
        LOG.info("Request Method {}", req.getMethod());
        LOG.info("ServerName {}", serverName);
        LOG.info("Request URI {}", req.getRequestURI());
        request.setInputRequest(ExceptionControllerUtils.getBody(req));
        request.setExceptionName(e.toString());
        request.setBodyContent(sw.toString());
        request.setRequestMethod(req.getMethod());
        request.setServer(serverName);
        request.setSeverity(SEVERITY);
        request.setServiceName(SERVICE_NAME_DE);
        request.setRequestURI(req.getRequestURI());
        request.setTime(java.time.LocalDateTime.now().toString());
        exceptionControllerUtils.makeExceptionAlertCall(request);
    }

    private String setServiceStatusForExceptionAlert(int statusId, String statusValue) {
        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setId(statusId);
        serviceStatus.setValue(statusValue);
        ServiceStatusResponse response = new ServiceStatusResponse(serviceStatus);
        return controllerHelper.generateJsonFromObj(response);
    }
}
