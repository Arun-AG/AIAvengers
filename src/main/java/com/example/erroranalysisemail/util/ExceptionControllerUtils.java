package com.example.erroranalysisemail.util;

import com.example.erroranalysisemail.model.EmailRequest;
import com.example.erroranalysisemail.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class ExceptionControllerUtils {
    
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionControllerUtils.class);
    
    @Autowired
    private EmailService emailService;
    
    public static String getBody(HttpServletRequest request) {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            // throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    // throw ex;
                }
            }
        }
        body = stringBuilder.toString();
        return body;
    }
    
    public void makeExceptionAlertCall(EmailRequest request) {
        try {
            LOG.info("Sending real-time exception alert for service: {}, exception: {}", 
                    request.getServiceName(), request.getExceptionName());
            LOG.info("Exception details - Server: {}, URI: {}, Method: {}, Time: {}", 
                    request.getServer(), request.getRequestURI(), request.getRequestMethod(), request.getTime());
            
            // Send actual email in real-time
            emailService.sendExceptionAlert(request);
            
        } catch (Exception e) {
            LOG.error("Failed to send exception alert", e);
        }
    }
}
