package com.example.erroranalysisemail.model;

import java.util.List;

public class EmailRequest {
    private String inputRequest;
    private String exceptionName;
    private String bodyContent;
    private String requestMethod;
    private String server;
    private String severity;
    private String serviceName;
    private String requestURI;
    private String time;
    
    // Enhanced analysis fields
    private String stackTraceAnalysis;
    private String gitCommitAnalysis;
    private List<String> relatedTicketIds;
    private String lastChangedBy;
    private String lastCommitMessage;
    private String correlationInsights;
    private String suggestedActions;
    
    // OpenAI analysis fields
    private String aiHumanReadableAnalysis;
    private String aiSuggestedFix;
    private String aiBusinessExplanation;

    // Getters and Setters
    public String getInputRequest() {
        return inputRequest;
    }

    public void setInputRequest(String inputRequest) {
        this.inputRequest = inputRequest;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    public String getBodyContent() {
        return bodyContent;
    }

    public void setBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    // Enhanced analysis getters and setters
    public String getStackTraceAnalysis() {
        return stackTraceAnalysis;
    }

    public void setStackTraceAnalysis(String stackTraceAnalysis) {
        this.stackTraceAnalysis = stackTraceAnalysis;
    }

    public String getGitCommitAnalysis() {
        return gitCommitAnalysis;
    }

    public void setGitCommitAnalysis(String gitCommitAnalysis) {
        this.gitCommitAnalysis = gitCommitAnalysis;
    }

    public List<String> getRelatedTicketIds() {
        return relatedTicketIds;
    }

    public void setRelatedTicketIds(List<String> relatedTicketIds) {
        this.relatedTicketIds = relatedTicketIds;
    }

    public String getLastChangedBy() {
        return lastChangedBy;
    }

    public void setLastChangedBy(String lastChangedBy) {
        this.lastChangedBy = lastChangedBy;
    }

    public String getLastCommitMessage() {
        return lastCommitMessage;
    }

    public void setLastCommitMessage(String lastCommitMessage) {
        this.lastCommitMessage = lastCommitMessage;
    }

    public String getCorrelationInsights() {
        return correlationInsights;
    }

    public void setCorrelationInsights(String correlationInsights) {
        this.correlationInsights = correlationInsights;
    }

    public String getSuggestedActions() {
        return suggestedActions;
    }

    public void setSuggestedActions(String suggestedActions) {
        this.suggestedActions = suggestedActions;
    }

    // OpenAI analysis getters and setters
    public String getAiHumanReadableAnalysis() {
        return aiHumanReadableAnalysis;
    }

    public void setAiHumanReadableAnalysis(String aiHumanReadableAnalysis) {
        this.aiHumanReadableAnalysis = aiHumanReadableAnalysis;
    }

    public String getAiSuggestedFix() {
        return aiSuggestedFix;
    }

    public void setAiSuggestedFix(String aiSuggestedFix) {
        this.aiSuggestedFix = aiSuggestedFix;
    }

    public String getAiBusinessExplanation() {
        return aiBusinessExplanation;
    }

    public void setAiBusinessExplanation(String aiBusinessExplanation) {
        this.aiBusinessExplanation = aiBusinessExplanation;
    }
}
