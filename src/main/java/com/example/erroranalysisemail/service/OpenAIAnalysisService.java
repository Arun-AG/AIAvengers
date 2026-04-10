package com.example.erroranalysisemail.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAIAnalysisService {
    
    private static final Logger LOG = LoggerFactory.getLogger(OpenAIAnalysisService.class);
    
    @Value("${openai.api.key:}")
    private String openAiApiKey;
    
    @Value("${openai.enabled:true}")
    private boolean openAiEnabled;
    
    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;
    
    @Value("${openai.timeout.seconds:30}")
    private int timeoutSeconds;
    
    @Value("${openai.max.tokens:1000}")
    private int maxTokens;
    
    private OpenAiService openAiService;
    
    public String analyzeStackTrace(String exceptionName, String stackTrace, String errorCategory) {
        if (!isEnabled()) {
            LOG.debug("OpenAI analysis is disabled or API key not configured");
            return null;
        }
        
        try {
            OpenAiService service = getOpenAiService();
            String prompt = buildAnalysisPrompt(exceptionName, stackTrace, errorCategory);
            
            LOG.info("Sending stack trace to OpenAI for analysis using model: {}", model);
            
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", 
                "You are an expert Java developer and debugging assistant. Analyze stack traces and provide clear, actionable explanations in simple language."));
            messages.add(new ChatMessage("user", prompt));
            
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(maxTokens)
                .temperature(0.3)
                .build();
            
            String response = service.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
            
            LOG.info("OpenAI analysis completed successfully");
            return formatAnalysis(response);
            
        } catch (Exception e) {
            LOG.error("Failed to analyze stack trace with OpenAI", e);
            return "AI analysis unavailable: " + e.getMessage();
        }
    }
    
    public String getSuggestedFix(String exceptionName, String stackTrace, String errorCategory) {
        if (!isEnabled()) return null;
        
        try {
            OpenAiService service = getOpenAiService();
            String prompt = buildFixSuggestionPrompt(exceptionName, stackTrace, errorCategory);
            
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", 
                "You are an expert Java developer. Provide practical, specific code fixes. Be concise and actionable."));
            messages.add(new ChatMessage("user", prompt));
            
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(maxTokens)
                .temperature(0.2)
                .build();
            
            return service.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
                
        } catch (Exception e) {
            LOG.error("Failed to get fix suggestions", e);
            return null;
        }
    }
    
    public String explainInBusinessTerms(String exceptionName, String errorMessage, String errorCategory) {
        if (!isEnabled()) return null;
        
        try {
            OpenAiService service = getOpenAiService();
            
            String prompt = String.format(
                "Explain this Java error in simple, non-technical business terms (2-3 sentences):\n\n" +
                "Error Type: %s\nError Message: %s\nCategory: %s\n\n" +
                "Focus on what went wrong and business impact.",
                exceptionName, errorMessage, errorCategory
            );
            
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", 
                "You are a technical translator. Convert technical errors into clear business language."));
            messages.add(new ChatMessage("user", prompt));
            
            ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(model)
                .messages(messages)
                .maxTokens(200)
                .temperature(0.3)
                .build();
            
            return service.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
                
        } catch (Exception e) {
            LOG.error("Failed to get business explanation", e);
            return null;
        }
    }
    
    private boolean isEnabled() {
        return openAiEnabled && openAiApiKey != null && !openAiApiKey.trim().isEmpty();
    }
    
    private synchronized OpenAiService getOpenAiService() {
        if (openAiService == null) {
            openAiService = new OpenAiService(openAiApiKey, Duration.ofSeconds(timeoutSeconds));
        }
        return openAiService;
    }
    
    private String buildAnalysisPrompt(String exceptionName, String stackTrace, String errorCategory) {
        return String.format(
            "Analyze this Java stack trace and provide a human-readable explanation:\n\n" +
            "Exception: %s\n" +
            "Category: %s\n" +
            "Stack Trace:\n%s\n\n" +
            "Provide your analysis in this format:\n\n" +
            "1. WHAT HAPPENED: (Brief description of the error in simple terms)\n" +
            "2. ROOT CAUSE: (What specifically went wrong)\n" +
            "3. LIKELY CAUSES: (3-5 bullet points of common causes for this type of error)\n" +
            "4. INVESTIGATION STEPS: (3-5 specific steps to diagnose the issue)\n" +
            "5. SEVERITY ASSESSMENT: (Low/Medium/High with brief justification)\n\n" +
            "Keep explanations clear and actionable for Java developers.",
            exceptionName, errorCategory, truncateStackTrace(stackTrace, 50)
        );
    }
    
    private String buildFixSuggestionPrompt(String exceptionName, String stackTrace, String errorCategory) {
        return String.format(
            "Suggest specific code fixes for this Java error:\n\n" +
            "Exception: %s\n" +
            "Category: %s\n" +
            "Stack Trace:\n%s\n\n" +
            "Provide:\n" +
            "1. IMMEDIATE FIX: (Quick code change to resolve the error)\n" +
            "2. PREVENTION: (How to prevent this in the future)\n" +
            "3. BEST PRACTICES: (Relevant coding patterns or libraries)\n\n" +
            "Be specific and include code examples where helpful.",
            exceptionName, errorCategory, truncateStackTrace(stackTrace, 30)
        );
    }
    
    private String truncateStackTrace(String stackTrace, int lines) {
        if (stackTrace == null) return "";
        String[] stackTraceLines = stackTrace.split("\n");
        if (stackTraceLines.length <= lines) return stackTrace;
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines; i++) {
            sb.append(stackTraceLines[i]).append("\n");
        }
        sb.append("... (truncated for analysis)");
        return sb.toString();
    }
    
    private String formatAnalysis(String analysis) {
        if (analysis == null) return null;
        return analysis.trim();
    }
}
