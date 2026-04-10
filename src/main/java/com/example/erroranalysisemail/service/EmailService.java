package com.example.erroranalysisemail.service;

import com.example.erroranalysisemail.config.ExceptionAlertConfig;
import com.example.erroranalysisemail.model.EmailRequest;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private ExceptionAlertConfig alertConfig;
    
    @Async
    public void sendExceptionAlert(EmailRequest emailRequest) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(alertConfig.getSenderEmail());
            
            // Convert recipient list to InternetAddress array
            String[] recipients = alertConfig.getRecipientEmails().toArray(new String[0]);
            helper.setTo(recipients);
            
            helper.setSubject(alertConfig.getEmailSubject());
            
            String emailContent = buildEmailContent(emailRequest);
            helper.setText(emailContent, true);
            
            mailSender.send(message);
            LOG.info("Exception alert email sent successfully to {} recipients: {}", 
                    recipients.length, String.join(", ", recipients));
            
        } catch (Exception e) {
            LOG.error("Failed to send exception alert email", e);
        }
    }
    
    private String buildEmailContent(EmailRequest request) {
        StringBuilder content = new StringBuilder();
        content.append("<html><body style='font-family: Arial, sans-serif;'>");
        content.append("<h2 style='color: #d32f2f;'>🚨 Exception Alert</h2>");
        content.append("<div style='background-color: #f5f5f5; padding: 15px; border-radius: 5px;'>");
        
        content.append("<p><strong>Service Name:</strong> ").append(request.getServiceName()).append("</p>");
        content.append("<p><strong>Server:</strong> ").append(request.getServer()).append("</p>");
        content.append("<p><strong>Time:</strong> ").append(request.getTime()).append("</p>");
        content.append("<p><strong>Severity:</strong> <span style='color: #d32f2f;'>").append(request.getSeverity()).append("</span></p>");
        content.append("<p><strong>Request Method:</strong> ").append(request.getRequestMethod()).append("</p>");
        content.append("<p><strong>Request URI:</strong> ").append(request.getRequestURI()).append("</p>");
        content.append("<p><strong>Exception Name:</strong> <span style='color: #d32f2f;'>").append(request.getExceptionName()).append("</span></p>");
        
        if (request.getInputRequest() != null && !request.getInputRequest().trim().isEmpty()) {
            content.append("<h3>Request Body:</h3>");
            content.append("<pre style='background-color: #e8e8e8; padding: 10px; border-radius: 3px; white-space: pre-wrap;'>")
                   .append(escapeHtml(request.getInputRequest())).append("</pre>");
        }
        
        content.append("<h3>Stack Trace:</h3>");
        content.append("<pre style='background-color: #ffebee; padding: 10px; border-radius: 3px; white-space: pre-wrap; color: #c62828;'>")
               .append(escapeHtml(request.getBodyContent())).append("</pre>");
        
        content.append("</div>");
        content.append("<p style='margin-top: 20px; font-size: 12px; color: #666;'>");
        content.append("This is an automated exception alert from Error Analysis Email Application.");
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
