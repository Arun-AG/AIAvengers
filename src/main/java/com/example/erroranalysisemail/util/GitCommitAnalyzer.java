package com.example.erroranalysisemail.util;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GitCommitAnalyzer {
    
    private static final Logger LOG = LoggerFactory.getLogger(GitCommitAnalyzer.class);
    
    private static final List<String> DEFAULT_TICKET_PATTERNS = List.of(
        "JIRA-", "TICKET-", "BUG-", "DE-", "ISSUE-", "TASK-", "FEATURE-", "HOTFIX-"
    );
    
    private static final Pattern TICKET_PATTERN = Pattern.compile(
        "(" + String.join("|", DEFAULT_TICKET_PATTERNS) + ")(\\d+)", 
        Pattern.CASE_INSENSITIVE
    );
    
    @Value("${git.repository.path:}")
    private String gitRepositoryPath;
    
    @Value("${git.analysis.enabled:true}")
    private boolean gitAnalysisEnabled;
    
    @Value("${git.analysis.days.back:7}")
    private int daysBack;
    
    public GitCommitAnalysis correlateErrorWithCommits(StackTraceElement rootCause) {
        GitCommitAnalysis analysis = new GitCommitAnalysis();
        
        if (!gitAnalysisEnabled || rootCause == null) {
            analysis.setAnalysisEnabled(false);
            analysis.setFullAnalysis("Git analysis is disabled or no root cause available");
            return analysis;
        }
        
        try {
            Repository repository = openRepository();
            if (repository == null) {
                analysis.setAnalysisEnabled(false);
                analysis.setFullAnalysis("Git repository not found or accessible");
                return analysis;
            }
            
            String filePath = extractFilePathFromStackTrace(rootCause);
            List<RevCommit> recentCommits = findRecentCommitsForFile(repository, filePath);
            
            if (recentCommits.isEmpty()) {
                analysis.setFullAnalysis("No recent commits found for this file");
            } else {
                populateAnalysis(analysis, recentCommits, filePath);
            }
            
            repository.close();
            
        } catch (Exception e) {
            LOG.error("Error during Git analysis", e);
            analysis.setAnalysisEnabled(false);
            analysis.setFullAnalysis("Git analysis failed: " + e.getMessage());
        }
        
        return analysis;
    }
    
    public List<RevCommit> findRecentCommitsForFile(Repository repository, String filePath) throws Exception {
        List<RevCommit> commits = new ArrayList<>();
        
        try (Git git = new Git(repository)) {
            Instant cutoffDate = Instant.now().minusSeconds(daysBack * 24L * 60L * 60L);
            
            Iterable<RevCommit> log = git.log()
                .addPath(filePath)
                .call();
            
            for (RevCommit commit : log) {
                if (commit.getCommitTime() < cutoffDate.getEpochSecond()) {
                    break;
                }
                commits.add(commit);
            }
        }
        
        return commits;
    }
    
    public List<String> extractTicketIdsFromCommit(String commitMessage) {
        List<String> ticketIds = new ArrayList<>();
        if (commitMessage == null) {
            return ticketIds;
        }
        
        Matcher matcher = TICKET_PATTERN.matcher(commitMessage);
        while (matcher.find()) {
            ticketIds.add(matcher.group());
        }
        
        return ticketIds;
    }
    
    public GitCommitDetails getCommitDetails(Repository repository, String filePath, String className) throws Exception {
        try (Git git = new Git(repository)) {
            Iterable<RevCommit> log = git.log()
                .addPath(filePath)
                .setMaxCount(1)
                .call();
            
            for (RevCommit commit : log) {
                GitCommitDetails details = new GitCommitDetails();
                details.setCommitId(commit.getName());
                details.setAuthor(commit.getAuthorIdent().getName());
                details.setAuthorEmail(commit.getAuthorIdent().getEmailAddress());
                details.setCommitMessage(commit.getFullMessage());
                details.setCommitDate(formatCommitDate(commit.getCommitTime()));
                details.setTicketIds(extractTicketIdsFromCommit(commit.getFullMessage()));
                return details;
            }
        }
        
        return null;
    }
    
    private Repository openRepository() {
        if (gitRepositoryPath == null || gitRepositoryPath.trim().isEmpty()) {
            return tryFindRepositoryFromCurrentDirectory();
        }
        
        try {
            File gitDir = new File(gitRepositoryPath, ".git");
            if (gitDir.exists()) {
                return new FileRepositoryBuilder()
                    .setGitDir(gitDir)
                    .readEnvironment()
                    .findGitDir()
                    .build();
            }
        } catch (IOException e) {
            LOG.error("Failed to open repository from configured path: {}", gitRepositoryPath, e);
        }
        
        return tryFindRepositoryFromCurrentDirectory();
    }
    
    private Repository tryFindRepositoryFromCurrentDirectory() {
        try {
            File currentDir = new File(".");
            File gitDir = findGitDirectory(currentDir);
            
            if (gitDir != null) {
                return new FileRepositoryBuilder()
                    .setGitDir(gitDir)
                    .readEnvironment()
                    .findGitDir()
                    .build();
            }
        } catch (IOException e) {
            LOG.error("Failed to find Git repository from current directory", e);
        }
        
        return null;
    }
    
    private File findGitDirectory(File directory) {
        File gitDir = new File(directory, ".git");
        if (gitDir.exists() && gitDir.isDirectory()) {
            return gitDir;
        }
        
        File parentDir = directory.getParentFile();
        if (parentDir != null) {
            return findGitDirectory(parentDir);
        }
        
        return null;
    }
    
    private String extractFilePathFromStackTrace(StackTraceElement element) {
        if (element == null) {
            return "";
        }
        
        String className = element.getClassName();
        String packageName = className.substring(0, className.lastIndexOf('.'));
        String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
        
        return "src/main/java/" + packageName.replace('.', '/') + "/" + simpleClassName + ".java";
    }
    
    private void populateAnalysis(GitCommitAnalysis analysis, List<RevCommit> commits, String filePath) {
        analysis.setAnalysisEnabled(true);
        
        if (!commits.isEmpty()) {
            RevCommit latestCommit = commits.get(0);
            analysis.setLastAuthor(latestCommit.getAuthorIdent().getName());
            analysis.setLastAuthorEmail(latestCommit.getAuthorIdent().getEmailAddress());
            analysis.setLastCommitDate(formatCommitDate(latestCommit.getCommitTime()));
            analysis.setLastCommitMessage(latestCommit.getFullMessage());
            analysis.setTicketIds(extractTicketIdsFromCommit(latestCommit.getFullMessage()));
        }
        
        analysis.setRecentChangesCount(commits.size());
        analysis.setFullAnalysis(buildFullAnalysis(analysis, commits, filePath));
    }
    
    private String formatCommitDate(int commitTime) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(commitTime), 
            ZoneId.systemDefault()
        );
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    private String buildFullAnalysis(GitCommitAnalysis analysis, List<RevCommit> commits, String filePath) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== GIT COMMIT ANALYSIS ===\n");
        
        if (analysis.getLastAuthor() != null) {
            sb.append("Last Changed: ").append(analysis.getLastCommitDate())
              .append(" by ").append(analysis.getLastAuthor())
              .append(" (").append(analysis.getLastAuthorEmail()).append(")\n");
            sb.append("Commit: \"").append(truncateMessage(analysis.getLastCommitMessage(), 100)).append("\"\n");
            
            if (!analysis.getTicketIds().isEmpty()) {
                sb.append("Ticket IDs: ").append(String.join(", ", analysis.getTicketIds())).append("\n");
            }
        }
        
        sb.append("Recent Changes in Last ").append(daysBack).append(" Days: ").append(commits.size()).append(" commits\n");
        
        for (int i = 0; i < Math.min(5, commits.size()); i++) {
            RevCommit commit = commits.get(i);
            List<String> tickets = extractTicketIdsFromCommit(commit.getFullMessage());
            String ticketInfo = tickets.isEmpty() ? "" : " - " + String.join(", ", tickets);
            sb.append("- ").append(formatCommitDate(commit.getCommitTime()))
              .append(": ").append(truncateMessage(commit.getShortMessage(), 50))
              .append(ticketInfo).append("\n");
        }
        
        return sb.toString();
    }
    
    private String truncateMessage(String message, int maxLength) {
        if (message == null) {
            return "";
        }
        if (message.length() <= maxLength) {
            return message;
        }
        return message.substring(0, maxLength) + "...";
    }
    
    public static class GitCommitAnalysis {
        private boolean analysisEnabled = true;
        private String lastAuthor;
        private String lastAuthorEmail;
        private String lastCommitDate;
        private String lastCommitMessage;
        private List<String> ticketIds = new ArrayList<>();
        private int recentChangesCount;
        private String fullAnalysis;
        
        public boolean isAnalysisEnabled() { return analysisEnabled; }
        public void setAnalysisEnabled(boolean analysisEnabled) { this.analysisEnabled = analysisEnabled; }
        
        public String getLastAuthor() { return lastAuthor; }
        public void setLastAuthor(String lastAuthor) { this.lastAuthor = lastAuthor; }
        
        public String getLastAuthorEmail() { return lastAuthorEmail; }
        public void setLastAuthorEmail(String lastAuthorEmail) { this.lastAuthorEmail = lastAuthorEmail; }
        
        public String getLastCommitDate() { return lastCommitDate; }
        public void setLastCommitDate(String lastCommitDate) { this.lastCommitDate = lastCommitDate; }
        
        public String getLastCommitMessage() { return lastCommitMessage; }
        public void setLastCommitMessage(String lastCommitMessage) { this.lastCommitMessage = lastCommitMessage; }
        
        public List<String> getTicketIds() { return ticketIds; }
        public void setTicketIds(List<String> ticketIds) { this.ticketIds = ticketIds; }
        
        public int getRecentChangesCount() { return recentChangesCount; }
        public void setRecentChangesCount(int recentChangesCount) { this.recentChangesCount = recentChangesCount; }
        
        public String getFullAnalysis() { return fullAnalysis; }
        public void setFullAnalysis(String fullAnalysis) { this.fullAnalysis = fullAnalysis; }
    }
    
    public static class GitCommitDetails {
        private String commitId;
        private String author;
        private String authorEmail;
        private String commitMessage;
        private String commitDate;
        private List<String> ticketIds = new ArrayList<>();
        
        public String getCommitId() { return commitId; }
        public void setCommitId(String commitId) { this.commitId = commitId; }
        
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        
        public String getAuthorEmail() { return authorEmail; }
        public void setAuthorEmail(String authorEmail) { this.authorEmail = authorEmail; }
        
        public String getCommitMessage() { return commitMessage; }
        public void setCommitMessage(String commitMessage) { this.commitMessage = commitMessage; }
        
        public String getCommitDate() { return commitDate; }
        public void setCommitDate(String commitDate) { this.commitDate = commitDate; }
        
        public List<String> getTicketIds() { return ticketIds; }
        public void setTicketIds(List<String> ticketIds) { this.ticketIds = ticketIds; }
    }
}
