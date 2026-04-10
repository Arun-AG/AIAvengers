package com.example.erroranalysisemail.controller;

import com.example.erroranalysisemail.model.EmailRequest;
import com.example.erroranalysisemail.model.ServiceStatus;
import com.example.erroranalysisemail.model.ServiceStatusResponse;
import com.example.erroranalysisemail.util.ControllerHelper;
import com.example.erroranalysisemail.util.ExceptionControllerUtils;
import com.example.erroranalysisemail.service.OpenAIAnalysisService;
import com.example.erroranalysisemail.util.GitCommitAnalyzer;
import com.example.erroranalysisemail.util.StackTraceAnalyzer;
import com.example.erroranalysisemail.util.Utils;
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

    @Autowired
    private StackTraceAnalyzer stackTraceAnalyzer;

    @Autowired
    private GitCommitAnalyzer gitCommitAnalyzer;

    @Autowired
    private OpenAIAnalysisService openAIAnalysisService;

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
        
        // Stack trace analysis
        StackTraceAnalyzer.StackTraceAnalysis stackAnalysis = stackTraceAnalyzer.analyzeStackTrace(e);
        LOG.info("Stack trace analysis completed: {} at {}", 
            stackAnalysis.getErrorCategory(), stackAnalysis.getRootCauseLocation());
        
        // Git commit analysis
        GitCommitAnalyzer.GitCommitAnalysis gitAnalysis = gitCommitAnalyzer.correlateErrorWithCommits(
            stackAnalysis.getRootCauseElement());
        LOG.info("Git commit analysis completed: {} related commits found", 
            gitAnalysis.isAnalysisEnabled() ? gitAnalysis.getRecentChangesCount() : "N/A");
        
        // OpenAI Analysis - Human readable explanation
        LOG.info("Starting OpenAI analysis for stack trace");
        String aiAnalysis = openAIAnalysisService.analyzeStackTrace(
            stackAnalysis.getExceptionType(), 
            sw.toString(), 
            stackAnalysis.getErrorCategory()
        );
        
        String aiSuggestedFix = openAIAnalysisService.getSuggestedFix(
            stackAnalysis.getExceptionType(),
            sw.toString(),
            stackAnalysis.getErrorCategory()
        );
        
        String aiBusinessExplanation = openAIAnalysisService.explainInBusinessTerms(
            stackAnalysis.getExceptionType(),
            stackAnalysis.getExceptionMessage(),
            stackAnalysis.getErrorCategory()
        );
        
        if (aiAnalysis != null) {
            LOG.info("OpenAI analysis completed successfully");
        } else {
            LOG.info("OpenAI analysis skipped (disabled or no API key)");
        }
        
        // Build correlation insights and suggested actions
        String correlationInsights = buildCorrelationInsights(stackAnalysis, gitAnalysis);
        String suggestedActions = buildSuggestedActions(stackAnalysis, gitAnalysis);
        
        request.setInputRequest(ExceptionControllerUtils.getBody(req));
        request.setExceptionName(e.toString());
        request.setBodyContent(sw.toString());
        request.setRequestMethod(req.getMethod());
        request.setServer(serverName);
        request.setSeverity(SEVERITY);
        request.setServiceName(SERVICE_NAME_DE);
        request.setRequestURI(req.getRequestURI());
        request.setTime(java.time.LocalDateTime.now().toString());
        
        // Enhanced analysis fields
        request.setStackTraceAnalysis(stackAnalysis.getFullAnalysis());
        request.setGitCommitAnalysis(gitAnalysis.getFullAnalysis());
        request.setRelatedTicketIds(gitAnalysis.getTicketIds());
        request.setLastChangedBy(gitAnalysis.getLastAuthor());
        request.setLastCommitMessage(gitAnalysis.getLastCommitMessage());
        request.setCorrelationInsights(correlationInsights);
        request.setSuggestedActions(suggestedActions);
        
        // OpenAI analysis fields
        request.setAiHumanReadableAnalysis(aiAnalysis);
        request.setAiSuggestedFix(aiSuggestedFix);
        request.setAiBusinessExplanation(aiBusinessExplanation);
        
        exceptionControllerUtils.makeExceptionAlertCall(request);
    }
    
    private String buildCorrelationInsights(StackTraceAnalyzer.StackTraceAnalysis stackAnalysis, 
                                          GitCommitAnalyzer.GitCommitAnalysis gitAnalysis) {
        StringBuilder insights = new StringBuilder();
        insights.append("=== CORRELATION INSIGHTS ===\n");
        
        if (gitAnalysis.isAnalysisEnabled() && gitAnalysis.getRecentChangesCount() > 0) {
            insights.append(String.format(
                "This error location was recently modified (%d commits in last 7 days)\n", 
                gitAnalysis.getRecentChangesCount()));
            
            if (!gitAnalysis.getTicketIds().isEmpty()) {
                insights.append(String.format("Related tickets: %s\n", 
                    String.join(", ", gitAnalysis.getTicketIds())));
            }
            
            if (gitAnalysis.getRecentChangesCount() <= 3) {
                insights.append("Recent code changes may have introduced this error\n");
            } else {
                insights.append("High activity in this file - potential regression\n");
            }
        } else {
            insights.append("No recent changes found for this error location\n");
            insights.append("This may be an existing bug or unrelated to recent commits\n");
        }
        
        insights.append(String.format("Error category: %s\n", stackAnalysis.getErrorCategory()));
        return insights.toString();
    }
    
    private String buildSuggestedActions(StackTraceAnalyzer.StackTraceAnalysis stackAnalysis, 
                                       GitCommitAnalyzer.GitCommitAnalysis gitAnalysis) {
        StringBuilder actions = new StringBuilder();
        actions.append("=== SUGGESTED ACTIONS ===\n");
        
        // Add git-related suggestions
        if (gitAnalysis.isAnalysisEnabled() && gitAnalysis.getLastAuthor() != null) {
            actions.append(String.format("- Contact %s about recent changes\n", 
                gitAnalysis.getLastAuthor()));
            
            if (!gitAnalysis.getTicketIds().isEmpty()) {
                actions.append(String.format("- Review tickets %s for context\n", 
                    String.join(", ", gitAnalysis.getTicketIds())));
            }
            
            if (gitAnalysis.getRecentChangesCount() <= 2) {
                actions.append("- Consider reverting recent changes if error persists\n");
            }
        }
        
        // Add category-specific suggestions
        String category = stackAnalysis.getErrorCategory();
        if (category.contains("Database")) {
            actions.append("- Check database connection pool settings\n");
            actions.append("- Verify database schema matches code expectations\n");
        } else if (category.contains("Network")) {
            actions.append("- Verify external service availability\n");
            actions.append("- Check network connectivity and timeouts\n");
        } else if (category.contains("Null Pointer")) {
            actions.append("- Add null checks in the affected method\n");
            actions.append("- Review object initialization sequence\n");
        } else if (category.contains("Business Logic")) {
            actions.append("- Validate input parameters against business rules\n");
            actions.append("- Review business rule validation logic\n");
        }
        
        // Always add general suggestions
        actions.append("- Add comprehensive logging around error location\n");
        actions.append(String.format("- Investigate %s for root cause\n", 
            stackAnalysis.getRootCauseLocation()));
        
        return actions.toString();
    }

    private String setServiceStatusForExceptionAlert(int statusId, String statusValue) {
        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setId(statusId);
        serviceStatus.setValue(statusValue);
        ServiceStatusResponse response = new ServiceStatusResponse(serviceStatus);
        return controllerHelper.generateJsonFromObj(response);
    }
}
