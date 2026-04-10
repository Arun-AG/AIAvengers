# Error Analysis Email Application

A Spring Boot application for error analysis and email notifications.

## Prerequisites

- Java 17 or later
- Maven 3.6 or later

## Setup

1. Clone or navigate to the project directory:
   ```bash
   cd D:/ErrorAnalysisEmail
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Configure email settings in `src/main/resources/application.properties`:
   - Update `spring.mail.username` with your email address
   - Update `spring.mail.password` with your app password

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints

- `GET /api/health` - Health check endpoint
- `GET /api/test` - Test endpoint

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
