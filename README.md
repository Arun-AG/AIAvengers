# Error Analysis Email Application

A Spring Boot application for real-time error analysis and email notifications with intelligent stack trace analysis and Git commit correlation.

## Prerequisites

- Java 17 or later
- Maven 3.6 or later

## Email Configuration

The application is pre-configured with Fastmail SMTP settings. Current configuration:

- **SMTP Server**: mail.messagingengine.com
- **SMTP Port**: 587
- **Sender**: postmaster@personifyfinancial.com
- **Recipients**:
  - kavithas@applieddatafinance.com
  - kirthikab@applieddatafinance.com
  - arunkumarag@applieddatafinance.com

## Setup

1. Clone or navigate to the project directory:
   ```bash
   cd D:/ErrorAnalysisEmail
   ```

2. Email configuration is already set in `src/main/resources/application.properties`:
   ```properties
   spring.mail.host=mail.messagingengine.com
   spring.mail.port=587
   spring.mail.username=postmaster@personifyfinancial.com
   spring.mail.password=g5d8sjk66j22xq8r
   exception.alert.recipient=kavithas@applieddatafinance.com,kirthikab@applieddatafinance.com
   exception.alert.sender=postmaster@personifyfinancial.com
   ```

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Enhanced Error Analysis Configuration

The application now includes intelligent error analysis with Git integration and OpenAI-powered analysis. Configure in `src/main/resources/application.properties`:

```properties
# Enable enhanced error analysis
error.analysis.enabled=true

# Git Analysis Configuration
git.analysis.enabled=true
git.repository.path=/path/to/your/repo  # Optional - auto-detects if empty
git.analysis.days.back=7
git.ticket.patterns=JIRA-,TICKET-,BUG-,DE-,ISSUE-,TASK-,FEATURE-,HOTFIX-,HACK-

# OpenAI Configuration (Optional)
openai.enabled=true
openai.api.key=sk-your-openai-api-key-here
openai.model=gpt-3.5-turbo
openai.max.tokens=1000
openai.timeout.seconds=30
```

**Note**: OpenAI integration is optional. If you don't provide an API key, the application will still work with all other features (stack trace analysis, Git correlation, etc.).

## API Endpoints

- `GET /api/health` - Health check endpoint
- `GET /api/test` - Test endpoint
- `GET /api/test-exception?type={runtime|illegal|null}` - Test exception handling (triggers email)

## Enhanced Exception Email Features

### Core Features
- **Real-time Email Alerts**: Automatically sends detailed exception emails to configured recipients
- **Rich HTML Templates**: Professional email formatting with color-coded sections
- **Async Processing**: Non-blocking email sending to prevent application slowdown
- **Comprehensive Details**: Includes request body, stack trace, server info, and timestamps

### 🔍 Stack Trace Analysis
- **Root Cause Detection**: Identifies the exact location where the error originated
- **Error Categorization**: Automatically classifies errors into categories:
  - Database Connection/Query Errors
  - Network/Connectivity Errors
  - Business Logic Errors
  - Null Pointer Errors
  - File I/O Errors
  - Timeout Errors
  - Security/Access Errors
  - General Application Errors
- **Call Pattern Extraction**: Shows the execution flow (Controller → Service → Repository)
- **File Path Resolution**: Maps stack trace to source file locations

### 📦 Git Commit Analysis
- **Commit Correlation**: Links errors to recent Git commits affecting the error location
- **Ticket ID Extraction**: Automatically extracts ticket references from commit messages
  - Supports: JIRA-, TICKET-, BUG-, DE-, ISSUE-, TASK-, FEATURE-, HOTFIX-, HACK- patterns
- **Author Identification**: Shows who last modified the problematic code
- **Change History**: Displays recent commits (configurable time window, default 7 days)

### 🤖 OpenAI-Powered Analysis (Optional)
- **Human-Readable Explanations**: Converts technical stack traces into plain English
  - What happened: Simple explanation of the error
  - Root cause: Specific technical reason
  - Likely causes: 3-5 common causes for this error type
  - Investigation steps: Specific diagnostic steps
  - Severity assessment: Low/Medium/High with justification
- **AI-Suggested Fixes**: Practical code fixes and prevention strategies
- **Business Impact Explanation**: Non-technical summary for stakeholders
  - What went wrong in business terms
  - Potential business impact

**Note**: OpenAI analysis requires a valid API key. Get one at https://platform.openai.com/api-keys

### 💡 Correlation Insights
- **Regression Detection**: Identifies if recent code changes may have introduced the error
- **Pattern Analysis**: Tracks error frequency in relation to code changes
- **Contextual Information**: Provides relevant ticket IDs and commit messages

### ✅ Suggested Actions
- **Developer Contact**: Recommends contacting the developer who made recent changes
- **Ticket Review**: Suggests reviewing related JIRA/ticket items for context
- **Category-Specific Advice**: Provides tailored recommendations based on error type:
  - Database errors: Check connection pools and schema
  - Network errors: Verify service availability
  - Null pointer errors: Add validation checks
  - Business logic errors: Review validation rules

## Email Content Includes

### Basic Information
- Service name and server information
- Request method, URI, and body
- Exception name and full stack trace
- Timestamp and severity level
- Color-coded HTML formatting for readability

### Enhanced Analysis Sections

#### 🔍 Stack Trace Analysis
- Root cause location with file path and line number
- Error category classification
- Exception type and message
- Call pattern (execution flow)

#### 🤖 OpenAI-Powered Analysis
- **Human-Readable Explanation**: Plain English breakdown of what went wrong
- **AI Suggested Fix**: Code-level recommendations to resolve the issue
- **Business Impact**: Non-technical explanation for stakeholders
- Powered by GPT-3.5-turbo or your configured model

#### 📦 Git Commit Analysis
- Related ticket badges (color-coded pills)
- Last author and commit information
- Recent changes history
- Configurable time window (default: last 7 days)

#### 💡 Correlation Insights
- Relationship between error and recent commits
- Potential regression indicators
- Error frequency analysis

#### ✅ Suggested Actions
- Developer contact recommendations
- Category-specific debugging steps
- Relevant ticket references
- Root cause investigation guidance

## Running Tests

```bash
mvn test
```

## Project Structure

```
ErrorAnalysisEmail/
├── src/
│   ├── main/
│   │   ├── java/com/example/erroranalysisemail/
│   │   │   ├── ErrorAnalysisEmailApplication.java
│   │   │   ├── config/
│   │   │   │   └── ExceptionAlertConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── ErrorAnalysisController.java
│   │   │   │   └── ExceptionalControllerAdvice.java    # Global exception handler with analysis
│   │   │   ├── model/
│   │   │   │   ├── EmailRequest.java                   # Enhanced with analysis fields
│   │   │   │   ├── ServiceStatus.java
│   │   │   │   └── ServiceStatusResponse.java
│   │   │   ├── service/
│   │   │   │   └── EmailService.java                   # Enhanced HTML email builder
│   │   │   └── util/
│   │   │       ├── ControllerHelper.java
│   │   │       ├── ExceptionControllerUtils.java
│   │   │       ├── GitCommitAnalyzer.java              # NEW: Git commit analysis
│   │   │       ├── StackTraceAnalyzer.java             # NEW: Stack trace analysis
│   │   │       └── Utils.java
│   │   └── resources/
│   │       ├── application.properties                  # Enhanced config
│   │       └── templates/
│   └── test/
│       └── java/com/example/erroranalysisemail/
│           ├── service/
│           │   ├── EmailServiceIntegrationTest.java
│           │   ├── EmailServiceTest.java
│           │   ├── EmailServiceUnitTest.java
│           │   └── StandaloneEmailTest.java
│           └── util/
│               ├── GitCommitAnalyzerTest.java          # NEW: Git analyzer tests
│               └── StackTraceAnalyzerTest.java         # NEW: Stack trace tests
└── pom.xml
```

## Dependencies

- **Spring Boot Web** - REST API and web functionality
- **Spring Boot Mail** - Email sending capabilities
- **Spring Boot Thymeleaf** - Template engine (optional)
- **Spring Boot Validation** - Input validation
- **Spring Boot Test** - Testing framework
- **Eclipse JGit** - Git repository analysis (`org.eclipse.jgit:6.8.0.202311291450-r`)
- **OpenAI Java Client** - AI-powered stack trace analysis (`com.theokanning.openai-gpt3-java:0.18.2`)

## How It Works

### Exception Flow

1. **Exception Occurs**: Any unhandled exception in the application is caught by `ExceptionalControllerAdvice`

2. **Stack Trace Analysis**: 
   - `StackTraceAnalyzer` processes the exception
   - Identifies root cause location in application code
   - Categorizes the error type
   - Extracts call pattern and file paths

3. **Git Commit Analysis**:
   - `GitCommitAnalyzer` correlates error location with recent commits
   - Searches Git history for changes to the affected file
   - Extracts ticket IDs from commit messages
   - Identifies the author of recent changes

4. **OpenAI Analysis** (Optional):
   - `OpenAIAnalysisService` sends the stack trace to OpenAI API
   - Receives human-readable explanation of the error
   - Gets suggested code fixes and business impact assessment
   - Gracefully degrades if no API key is configured

5. **Enhanced Email Generation:
   - `EmailService` builds a comprehensive HTML email
   - Includes all analysis sections with visual formatting
   - Adds correlation insights and suggested actions
   - Sends asynchronously to configured recipients

### Error Categories

| Category | Description | Common Causes |
|----------|-------------|---------------|
| Database | SQL and data access issues | Connection failures, query errors, timeouts |
| Network | External service issues | API failures, timeouts, DNS issues |
| Business Logic | Validation and rule violations | Invalid inputs, state errors |
| Null Pointer | Missing object references | Uninitialized variables, missing null checks |
| File I/O | File system operations | Permission issues, missing files, disk space |
| Timeout | Operation time limits | Slow services, infinite loops |
| Security | Access and authentication | Unauthorized access, token expiration |

## Testing the Enhanced Features

### Test Exception Endpoints

```bash
# Test different exception types to see analysis
GET /api/test-exception?type=runtime    # RuntimeException
GET /api/test-exception?type=illegal    # IllegalArgumentException  
GET /api/test-exception?type=null       # NullPointerException
```

Each exception type will trigger:
1. Different error categorization
2. Relevant stack trace analysis
3. Git correlation (if applicable)
4. Category-specific suggested actions

### Unit Tests

Run the new utility tests:

```bash
# Stack trace analyzer tests
mvn test -Dtest=StackTraceAnalyzerTest

# Git commit analyzer tests
mvn test -Dtest=GitCommitAnalyzerTest

# All tests
mvn test
```

## Benefits

- **Complete Context**: Error + AI Analysis + Code changes + Ticket references in one email
- **Faster Resolution**: Human-readable explanations reduce debugging time
- **AI-Powered Insights**: OpenAI explains complex errors in simple terms
- **Direct Action Items**: Specific code fixes suggested by AI
- **Regression Detection**: Identifies if recent changes caused issues
- **Accountability**: Knows who changed the problematic code
- **Intelligent Guidance**: Category-specific debugging suggestions
- **Historical Analysis**: Tracks error patterns over time
- **Business Communication**: Non-technical explanations for stakeholders

## Configuration Examples

### Enable/Disable Features

```properties
# Disable all analysis (fallback to basic emails)
error.analysis.enabled=false
git.analysis.enabled=false
openai.enabled=false

# Enable only stack trace analysis
git.analysis.enabled=false
openai.enabled=false

# Enable only OpenAI analysis (disable Git)
openai.enabled=true
git.analysis.enabled=false

# Extend Git history search
git.analysis.days.back=14

# Custom ticket patterns
git.ticket.patterns=PROJ-,CRM-,API-

# OpenAI Configuration
openai.enabled=true
openai.api.key=sk-your-api-key
openai.model=gpt-4  # Use GPT-4 for better analysis
openai.max.tokens=2000  # Increase for longer responses
```

### Multiple Git Repositories

If your code spans multiple repositories, configure the main one:

```properties
# Point to the primary repository
git.repository.path=/home/user/projects/main-app

# Or leave empty to auto-detect from current directory
git.repository.path=
```
