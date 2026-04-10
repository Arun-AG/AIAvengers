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
        
        // Enhanced Analysis Section
        content.append("<h2 style='color: #1976d2; margin-top: 25px;'>📊 Enhanced Error Analysis</h2>");
        
        // OpenAI Analysis - Human Readable Explanation
        if (request.getAiHumanReadableAnalysis() != null && !request.getAiHumanReadableAnalysis().trim().isEmpty()) {
            content.append("<div style='background-color: #fff8e1; padding: 15px; border-radius: 5px; margin-top: 15px; border-left: 4px solid #ff8f00;'>");
            content.append("<h3 style='color: #e65100;'>🤖 AI-Powered Analysis</h3>");
            content.append("<p style='font-style: italic; color: #666; margin-bottom: 10px;'>Powered by OpenAI - Human-readable explanation of the error</p>");
            content.append("<div style='background-color: #fff; padding: 12px; border-radius: 3px; white-space: pre-wrap; line-height: 1.6;'>").append(escapeHtml(request.getAiHumanReadableAnalysis())).append("</div>");
            
            // AI Suggested Fix (if available)
            if (request.getAiSuggestedFix() != null && !request.getAiSuggestedFix().trim().isEmpty()) {
                content.append("<h4 style='color: #2e7d32; margin-top: 15px;'>🔧 AI Suggested Fix</h4>");
                content.append("<div style='background-color: #f1f8e9; padding: 10px; border-radius: 3px; white-space: pre-wrap; border-left: 3px solid #558b2f;'>").append(escapeHtml(request.getAiSuggestedFix())).append("</div>");
            }
            
            // Business Explanation (if available)
            if (request.getAiBusinessExplanation() != null && !request.getAiBusinessExplanation().trim().isEmpty()) {
                content.append("<h4 style='color: #1565c0; margin-top: 15px;'>💼 Business Impact Explanation</h4>");
                content.append("<div style='background-color: #e3f2fd; padding: 10px; border-radius: 3px; font-style: italic; border-left: 3px solid #1976d2;'>").append(escapeHtml(request.getAiBusinessExplanation())).append("</div>");
            }
            
            content.append("</div>");
        }
        
        // Stack Trace Analysis
        if (request.getStackTraceAnalysis() != null && !request.getStackTraceAnalysis().trim().isEmpty()) {
            content.append("<div style='background-color: #e3f2fd; padding: 15px; border-radius: 5px; margin-top: 15px;'>");
            content.append("<h3 style='color: #1976d2;'>🔍 Stack Trace Analysis</h3>");
            content.append("<pre style='background-color: #f5f5f5; padding: 10px; border-radius: 3px; white-space: pre-wrap;'>").append(escapeHtml(request.getStackTraceAnalysis())).append("</pre>");
            content.append("</div>");
        }
        
        // Git Commit Analysis
        if (request.getGitCommitAnalysis() != null && !request.getGitCommitAnalysis().trim().isEmpty()) {
            content.append("<div style='background-color: #f3e5f5; padding: 15px; border-radius: 5px; margin-top: 15px;'>");
            content.append("<h3 style='color: #7b1fa2;'>📦 Git Commit Analysis</h3>");
            
            // Related Tickets Badge
            if (request.getRelatedTicketIds() != null && !request.getRelatedTicketIds().isEmpty()) {
                content.append("<p><strong>🔗 Related Tickets:</strong> ");
                for (String ticket : request.getRelatedTicketIds()) {
                    content.append("<span style='background-color: #9c27b0; color: white; padding: 3px 8px; border-radius: 12px; margin-right: 5px; font-size: 12px;'>").append(ticket).append("</span>");
                }
                content.append("</p>");
            }
            
            // Last Changed By
            if (request.getLastChangedBy() != null) {
                content.append("<p><strong>👤 Last Changed By:</strong> ").append(escapeHtml(request.getLastChangedBy())).append("</p>");
            }
            
            // Last Commit Message
            if (request.getLastCommitMessage() != null) {
                content.append("<p><strong>💬 Last Commit:</strong> <em>\"").append(escapeHtml(request.getLastCommitMessage())).append("\"</em></p>");
            }
            
            content.append("<pre style='background-color: #f5f5f5; padding: 10px; border-radius: 3px; white-space: pre-wrap;'>").append(escapeHtml(request.getGitCommitAnalysis())).append("</pre>");
            content.append("</div>");
        }
        
        // Correlation Insights
        if (request.getCorrelationInsights() != null && !request.getCorrelationInsights().trim().isEmpty()) {
            content.append("<div style='background-color: #fff3e0; padding: 15px; border-radius: 5px; margin-top: 15px;'>");
            content.append("<h3 style='color: #ef6c00;'>💡 Correlation Insights</h3>");
            content.append("<pre style='background-color: #f5f5f5; padding: 10px; border-radius: 3px; white-space: pre-wrap;'>").append(escapeHtml(request.getCorrelationInsights())).append("</pre>");
            content.append("</div>");
        }
        
        // Suggested Actions
        if (request.getSuggestedActions() != null && !request.getSuggestedActions().trim().isEmpty()) {
            content.append("<div style='background-color: #e8f5e9; padding: 15px; border-radius: 5px; margin-top: 15px;'>");
            content.append("<h3 style='color: #2e7d32;'>✅ Suggested Actions</h3>");
            content.append("<pre style='background-color: #f5f5f5; padding: 10px; border-radius: 3px; white-space: pre-wrap;'>").append(escapeHtml(request.getSuggestedActions())).append("</pre>");
            content.append("</div>");
        }
        
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
