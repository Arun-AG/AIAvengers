package com.example.erroranalysisemail.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StackTraceAnalyzer {
    
    private static final List<String> DATABASE_EXCEPTIONS = Arrays.asList(
        "SQLException", "DatabaseException", "DataAccessException", 
        "JpaSystemException", "QueryTimeoutException"
    );
    
    private static final List<String> NETWORK_EXCEPTIONS = Arrays.asList(
        "ConnectException", "SocketTimeoutException", "UnknownHostException",
        "HttpServerErrorException", "ResourceAccessException"
    );
    
    private static final List<String> BUSINESS_LOGIC_EXCEPTIONS = Arrays.asList(
        "BusinessException", "ValidationException", "IllegalArgumentException",
        "IllegalStateException", "BusinessRuleException"
    );
    
    private static final List<String> NULL_POINTER_EXCEPTIONS = Arrays.asList(
        "NullPointerException", "NullPointerException"
    );
    
    private static final List<String> IO_EXCEPTIONS = Arrays.asList(
        "IOException", "FileNotFoundException", "FileAccessException"
    );

    public StackTraceAnalysis analyzeStackTrace(Throwable exception) {
        StackTraceAnalysis analysis = new StackTraceAnalysis();
        
        StackTraceElement[] stackTrace = exception.getStackTrace();
        StackTraceElement rootCause = findRootCause(stackTrace);
        
        analysis.setRootCauseElement(rootCause);
        analysis.setExceptionType(exception.getClass().getSimpleName());
        analysis.setExceptionMessage(exception.getMessage());
        analysis.setRootCauseLocation(formatRootCauseLocation(rootCause));
        analysis.setFilePath(extractFilePath(rootCause));
        analysis.setErrorCategory(categorizeException(exception.getClass().getSimpleName()));
        analysis.setCallPattern(extractCallPattern(stackTrace, rootCause));
        analysis.setFullAnalysis(buildFullAnalysis(analysis));
        
        return analysis;
    }
    
    public String categorizeException(String exceptionClassName) {
        if (DATABASE_EXCEPTIONS.stream().anyMatch(exceptionClassName::contains)) {
            return "Database Connection/Query Error";
        }
        if (NETWORK_EXCEPTIONS.stream().anyMatch(exceptionClassName::contains)) {
            return "Network/Connectivity Error";
        }
        if (BUSINESS_LOGIC_EXCEPTIONS.stream().anyMatch(exceptionClassName::contains)) {
            return "Business Logic Error";
        }
        if (NULL_POINTER_EXCEPTIONS.stream().anyMatch(exceptionClassName::contains)) {
            return "Null Pointer Error";
        }
        if (IO_EXCEPTIONS.stream().anyMatch(exceptionClassName::contains)) {
            return "File I/O Error";
        }
        if (exceptionClassName.contains("Timeout")) {
            return "Timeout Error";
        }
        if (exceptionClassName.contains("Security") || exceptionClassName.contains("Access")) {
            return "Security/Access Error";
        }
        return "General Application Error";
    }
    
    public StackTraceElement findRootCause(StackTraceElement[] stackTrace) {
        if (stackTrace == null || stackTrace.length == 0) {
            return null;
        }
        
        for (StackTraceElement element : stackTrace) {
            if (isApplicationCode(element.getClassName())) {
                return element;
            }
        }
        
        return stackTrace[0];
    }
    
    public String extractFilePath(StackTraceElement element) {
        if (element == null) {
            return "Unknown";
        }
        
        String className = element.getClassName();
        String packageName = className.substring(0, className.lastIndexOf('.'));
        String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
        
        return packageName.replace('.', '/') + "/" + simpleClassName + ".java";
    }
    
    public String extractCallPattern(StackTraceElement[] stackTrace, StackTraceElement rootCause) {
        if (stackTrace == null || stackTrace.length == 0) {
            return "Unknown";
        }
        
        List<String> callTypes = Arrays.stream(stackTrace)
            .limit(10)
            .filter(this::isApplicationCode)
            .map(element -> getLayerType(element.getClassName()))
            .distinct()
            .collect(Collectors.toList());
        
        return String.join(" → ", callTypes);
    }
    
    private String formatRootCauseLocation(StackTraceElement rootCause) {
        if (rootCause == null) {
            return "Unknown location";
        }
        return String.format("%s.%s() line %d", 
            rootCause.getClassName(), 
            rootCause.getMethodName(), 
            rootCause.getLineNumber());
    }
    
    private boolean isApplicationCode(String className) {
        return className.startsWith("com.example") || 
               className.startsWith("com.adf") ||
               !className.startsWith("java.") &&
               !className.startsWith("javax.") &&
               !className.startsWith("org.springframework.") &&
               !className.startsWith("org.apache.") &&
               !className.startsWith("sun.") &&
               !className.startsWith("jdk.");
    }
    
    private String getLayerType(String className) {
        if (className.contains("Controller") || className.contains("RestController")) {
            return "Controller";
        }
        if (className.contains("Service") || className.contains("ServiceImpl")) {
            return "Service";
        }
        if (className.contains("Repository") || className.contains("Dao")) {
            return "Repository";
        }
        if (className.contains("Util") || className.contains("Helper")) {
            return "Utility";
        }
        if (className.contains("Config") || className.contains("Configuration")) {
            return "Configuration";
        }
        return "Component";
    }
    
    private String buildFullAnalysis(StackTraceAnalysis analysis) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== STACK TRACE ANALYSIS ===\n");
        sb.append("Root Cause: ").append(analysis.getRootCauseLocation()).append("\n");
        sb.append("File: ").append(analysis.getFilePath()).append("\n");
        sb.append("Error Category: ").append(analysis.getErrorCategory()).append("\n");
        sb.append("Exception Type: ").append(analysis.getExceptionType()).append("\n");
        sb.append("Call Pattern: ").append(analysis.getCallPattern()).append("\n");
        if (analysis.getExceptionMessage() != null) {
            sb.append("Error Message: ").append(analysis.getExceptionMessage()).append("\n");
        }
        return sb.toString();
    }
    
    public static class StackTraceAnalysis {
        private StackTraceElement rootCauseElement;
        private String exceptionType;
        private String exceptionMessage;
        private String rootCauseLocation;
        private String filePath;
        private String errorCategory;
        private String callPattern;
        private String fullAnalysis;
        
        public StackTraceElement getRootCauseElement() { return rootCauseElement; }
        public void setRootCauseElement(StackTraceElement rootCauseElement) { this.rootCauseElement = rootCauseElement; }
        
        public String getExceptionType() { return exceptionType; }
        public void setExceptionType(String exceptionType) { this.exceptionType = exceptionType; }
        
        public String getExceptionMessage() { return exceptionMessage; }
        public void setExceptionMessage(String exceptionMessage) { this.exceptionMessage = exceptionMessage; }
        
        public String getRootCauseLocation() { return rootCauseLocation; }
        public void setRootCauseLocation(String rootCauseLocation) { this.rootCauseLocation = rootCauseLocation; }
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public String getErrorCategory() { return errorCategory; }
        public void setErrorCategory(String errorCategory) { this.errorCategory = errorCategory; }
        
        public String getCallPattern() { return callPattern; }
        public void setCallPattern(String callPattern) { this.callPattern = callPattern; }
        
        public String getFullAnalysis() { return fullAnalysis; }
        public void setFullAnalysis(String fullAnalysis) { this.fullAnalysis = fullAnalysis; }
    }
}
