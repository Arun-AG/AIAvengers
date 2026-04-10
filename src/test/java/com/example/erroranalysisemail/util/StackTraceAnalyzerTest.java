package com.example.erroranalysisemail.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StackTraceAnalyzerTest {

    private final StackTraceAnalyzer analyzer = new StackTraceAnalyzer();

    @Test
    void testAnalyzeStackTraceWithNullPointerException() {
        try {
            String nullString = null;
            nullString.length();
        } catch (NullPointerException e) {
            StackTraceAnalyzer.StackTraceAnalysis analysis = analyzer.analyzeStackTrace(e);
            
            assertNotNull(analysis);
            assertEquals("NullPointerException", analysis.getExceptionType());
            assertEquals("Null Pointer Error", analysis.getErrorCategory());
            assertNotNull(analysis.getRootCauseElement());
            assertNotNull(analysis.getFilePath());
            assertTrue(analysis.getFullAnalysis().contains("STACK TRACE ANALYSIS"));
        }
    }

    @Test
    void testAnalyzeStackTraceWithIllegalArgumentException() {
        try {
            throw new IllegalArgumentException("Invalid argument provided");
        } catch (IllegalArgumentException e) {
            StackTraceAnalyzer.StackTraceAnalysis analysis = analyzer.analyzeStackTrace(e);
            
            assertNotNull(analysis);
            assertEquals("IllegalArgumentException", analysis.getExceptionType());
            assertEquals("Business Logic Error", analysis.getErrorCategory());
            assertEquals("Invalid argument provided", analysis.getExceptionMessage());
        }
    }

    @Test
    void testCategorizeException() {
        assertEquals("Database Connection/Query Error", analyzer.categorizeException("SQLException"));
        assertEquals("Database Connection/Query Error", analyzer.categorizeException("DataAccessException"));
        assertEquals("Network/Connectivity Error", analyzer.categorizeException("ConnectException"));
        assertEquals("Business Logic Error", analyzer.categorizeException("BusinessException"));
        assertEquals("Null Pointer Error", analyzer.categorizeException("NullPointerException"));
        assertEquals("File I/O Error", analyzer.categorizeException("IOException"));
        assertEquals("Timeout Error", analyzer.categorizeException("SocketTimeoutException"));
        assertEquals("Security/Access Error", analyzer.categorizeException("AccessDeniedException"));
        assertEquals("General Application Error", analyzer.categorizeException("SomeRandomException"));
    }

    @Test
    void testFindRootCause() {
        StackTraceElement[] stackTrace = new StackTraceElement[] {
            new StackTraceElement("java.util.ArrayList", "get", "ArrayList.java", 123),
            new StackTraceElement("com.example.service.UserService", "findUser", "UserService.java", 45),
            new StackTraceElement("com.example.controller.UserController", "getUser", "UserController.java", 30)
        };
        
        StackTraceElement rootCause = analyzer.findRootCause(stackTrace);
        
        assertNotNull(rootCause);
        assertEquals("com.example.service.UserService", rootCause.getClassName());
    }

    @Test
    void testFindRootCauseWithOnlyFrameworkCode() {
        StackTraceElement[] stackTrace = new StackTraceElement[] {
            new StackTraceElement("java.util.ArrayList", "get", "ArrayList.java", 123),
            new StackTraceElement("org.springframework.web.DispatcherServlet", "doDispatch", "DispatcherServlet.java", 999)
        };
        
        StackTraceElement rootCause = analyzer.findRootCause(stackTrace);
        
        // Should return first element if no application code found
        assertNotNull(rootCause);
    }

    @Test
    void testFindRootCauseWithEmptyArray() {
        StackTraceElement[] emptyArray = new StackTraceElement[0];
        assertNull(analyzer.findRootCause(emptyArray));
    }

    @Test
    void testFindRootCauseWithNull() {
        assertNull(analyzer.findRootCause(null));
    }

    @Test
    void testExtractFilePath() {
        StackTraceElement element = new StackTraceElement(
            "com.example.erroranalysisemail.service.EmailService", 
            "sendExceptionAlert", 
            "EmailService.java", 
            25
        );
        
        String filePath = analyzer.extractFilePath(element);
        
        assertEquals("com/example/erroranalysisemail/service/EmailService.java", filePath);
    }

    @Test
    void testExtractFilePathWithNull() {
        assertEquals("Unknown", analyzer.extractFilePath(null));
    }

    @Test
    void testExtractCallPattern() {
        StackTraceElement[] stackTrace = new StackTraceElement[] {
            new StackTraceElement("com.example.controller.UserController", "getUser", "UserController.java", 30),
            new StackTraceElement("com.example.service.UserService", "findUser", "UserService.java", 45),
            new StackTraceElement("com.example.repository.UserRepository", "findById", "UserRepository.java", 20)
        };
        
        String callPattern = analyzer.extractCallPattern(stackTrace, stackTrace[0]);
        
        assertTrue(callPattern.contains("Controller"));
        assertTrue(callPattern.contains("Service"));
        assertTrue(callPattern.contains("Repository"));
    }

    @Test
    void testExtractCallPatternWithEmptyArray() {
        assertEquals("Unknown", analyzer.extractCallPattern(new StackTraceElement[0], null));
    }

    @Test
    void testNestedException() {
        Exception rootCause = new IllegalStateException("Root cause");
        Exception wrapped = new RuntimeException("Wrapped", rootCause);
        
        StackTraceAnalyzer.StackTraceAnalysis analysis = analyzer.analyzeStackTrace(wrapped);
        
        assertNotNull(analysis);
        assertEquals("RuntimeException", analysis.getExceptionType());
        assertEquals("Wrapped", analysis.getExceptionMessage());
    }
}
