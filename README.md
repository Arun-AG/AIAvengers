# Error Analysis Email Application

A Spring Boot application for real-time error analysis and email notifications.

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

## API Endpoints

- `GET /api/health` - Health check endpoint
- `GET /api/test` - Test endpoint
- `GET /api/test-exception?type={runtime|illegal|null}` - Test exception handling (triggers email)

## Exception Email Features

- **Real-time Email Alerts**: Automatically sends detailed exception emails to `kavithas31032000@gmail.com`
- **Rich HTML Templates**: Professional email formatting with exception details
- **Async Processing**: Non-blocking email sending to prevent application slowdown
- **Comprehensive Details**: Includes request body, stack trace, server info, and timestamps

## Email Content Includes

- Service name and server information
- Request method, URI, and body
- Exception name and full stack trace
- Timestamp and severity level
- Color-coded HTML formatting for readability

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
│   │   │   └── controller/
│   │   │       └── ErrorAnalysisController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/erroranalysisemail/
└── pom.xml
```

## Dependencies

- Spring Boot Web
- Spring Boot Mail
- Spring Boot Thymeleaf
- Spring Boot Validation
- Spring Boot Test
