package com.example.erroranalysisemail.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class GitCommitAnalyzerTest {

    private final GitCommitAnalyzer analyzer = new GitCommitAnalyzer();

    @Test
    void testExtractTicketIdsFromCommitWithJira() {
        String commitMessage = "Fix payment processing timeout issue - JIRA-1234 and JIRA-1256";
        List<String> ticketIds = analyzer.extractTicketIdsFromCommit(commitMessage);
        
        assertEquals(2, ticketIds.size());
        assertTrue(ticketIds.contains("JIRA-1234"));
        assertTrue(ticketIds.contains("JIRA-1256"));
    }

    @Test
    void testExtractTicketIdsFromCommitWithMultiplePatterns() {
        String commitMessage = "Fix issue - JIRA-123, TICKET-456, BUG-789, DE-001";
        List<String> ticketIds = analyzer.extractTicketIdsFromCommit(commitMessage);
        
        assertEquals(4, ticketIds.size());
        assertTrue(ticketIds.contains("JIRA-123"));
        assertTrue(ticketIds.contains("TICKET-456"));
        assertTrue(ticketIds.contains("BUG-789"));
        assertTrue(ticketIds.contains("DE-001"));
    }

    @Test
    void testExtractTicketIdsFromCommitWithCaseInsensitivity() {
        String commitMessage = "Fix - jira-123, ticket-456";
        List<String> ticketIds = analyzer.extractTicketIdsFromCommit(commitMessage);
        
        assertEquals(2, ticketIds.size());
        assertTrue(ticketIds.contains("jira-123"));
        assertTrue(ticketIds.contains("ticket-456"));
    }

    @Test
    void testExtractTicketIdsFromNullCommit() {
        List<String> ticketIds = analyzer.extractTicketIdsFromCommit(null);
        assertTrue(ticketIds.isEmpty());
    }

    @Test
    void testExtractTicketIdsFromEmptyCommit() {
        List<String> ticketIds = analyzer.extractTicketIdsFromCommit("");
        assertTrue(ticketIds.isEmpty());
    }

    @Test
    void testExtractTicketIdsFromCommitWithoutTickets() {
        String commitMessage = "Just a regular commit message without any ticket references";
        List<String> ticketIds = analyzer.extractTicketIdsFromCommit(commitMessage);
        
        assertTrue(ticketIds.isEmpty());
    }

    @Test
    void testExtractTicketIdsFromCommitWithIssuePattern() {
        String commitMessage = "ISSUE-42 Fix the bug in user authentication";
        List<String> ticketIds = analyzer.extractTicketIdsFromCommit(commitMessage);
        
        assertEquals(1, ticketIds.size());
        assertEquals("ISSUE-42", ticketIds.get(0));
    }

    @Test
    void testExtractTicketIdsFromCommitWithTaskPattern() {
        String commitMessage = "TASK-999 Update documentation";
        List<String> ticketIds = analyzer.extractTicketIdsFromCommit(commitMessage);
        
        assertEquals(1, ticketIds.size());
        assertEquals("TASK-999", ticketIds.get(0));
    }

    @Test
    void testExtractTicketIdsFromCommitWithFeaturePattern() {
        String commitMessage = "FEATURE-101 Implement new search functionality";
        List<String> ticketIds = analyzer.extractTicketIdsFromCommit(commitMessage);
        
        assertEquals(1, ticketIds.size());
        assertEquals("FEATURE-101", ticketIds.get(0));
    }

    @Test
    void testExtractTicketIdsFromCommitWithHotfixPattern() {
        String commitMessage = "HOTFIX-2024 Fix critical production bug";
        List<String> ticketIds = analyzer.extractTicketIdsFromCommit(commitMessage);
        
        assertEquals(1, ticketIds.size());
        assertEquals("HOTFIX-2024", ticketIds.get(0));
    }

    @Test
    void testExtractTicketIdsWithHACKPattern() {
        String commitMessage = "Fix temp issue - HACK-123";
        List<String> ticketIds = analyzer.extractTicketIdsFromCommit(commitMessage);
        
        assertEquals(1, ticketIds.size());
        assertEquals("HACK-123", ticketIds.get(0));
    }

    @Test
    void testGitCommitAnalysisInitialization() {
        GitCommitAnalyzer.GitCommitAnalysis analysis = new GitCommitAnalyzer.GitCommitAnalysis();
        
        assertTrue(analysis.isAnalysisEnabled());
        assertNotNull(analysis.getTicketIds());
        assertTrue(analysis.getTicketIds().isEmpty());
        assertEquals(0, analysis.getRecentChangesCount());
    }

    @Test
    void testGitCommitAnalysisSettersAndGetters() {
        GitCommitAnalyzer.GitCommitAnalysis analysis = new GitCommitAnalyzer.GitCommitAnalysis();
        
        analysis.setLastAuthor("John Doe");
        analysis.setLastAuthorEmail("john@example.com");
        analysis.setLastCommitDate("2024-01-15 10:30:00");
        analysis.setLastCommitMessage("Test commit message");
        analysis.setRecentChangesCount(5);
        analysis.setAnalysisEnabled(true);
        analysis.setFullAnalysis("Test analysis");
        
        assertEquals("John Doe", analysis.getLastAuthor());
        assertEquals("john@example.com", analysis.getLastAuthorEmail());
        assertEquals("2024-01-15 10:30:00", analysis.getLastCommitDate());
        assertEquals("Test commit message", analysis.getLastCommitMessage());
        assertEquals(5, analysis.getRecentChangesCount());
        assertTrue(analysis.isAnalysisEnabled());
        assertEquals("Test analysis", analysis.getFullAnalysis());
    }

    @Test
    void testGitCommitDetailsSettersAndGetters() {
        GitCommitAnalyzer.GitCommitDetails details = new GitCommitAnalyzer.GitCommitDetails();
        
        details.setCommitId("abc123");
        details.setAuthor("Jane Doe");
        details.setAuthorEmail("jane@example.com");
        details.setCommitMessage("Test commit");
        details.setCommitDate("2024-01-15");
        
        assertEquals("abc123", details.getCommitId());
        assertEquals("Jane Doe", details.getAuthor());
        assertEquals("jane@example.com", details.getAuthorEmail());
        assertEquals("Test commit", details.getCommitMessage());
        assertEquals("2024-01-15", details.getCommitDate());
    }

    @Test
    void testCorrelateErrorWithCommitsDisabled() {
        // Create analyzer with disabled flag - this would require mocking or setting value
        // For now, just test the method signature and basic behavior
        StackTraceElement element = new StackTraceElement(
            "com.example.TestClass", "testMethod", "TestClass.java", 10
        );
        
        GitCommitAnalyzer.GitCommitAnalysis analysis = analyzer.correlateErrorWithCommits(element);
        
        assertNotNull(analysis);
        // Result depends on whether git analysis is enabled and repo is accessible
    }

    @Test
    void testCorrelateErrorWithNullRootCause() {
        GitCommitAnalyzer.GitCommitAnalysis analysis = analyzer.correlateErrorWithCommits(null);
        
        assertNotNull(analysis);
        assertFalse(analysis.isAnalysisEnabled());
    }
}
