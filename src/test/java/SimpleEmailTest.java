import org.springframework.mail.javamail.JavaMailSenderImpl;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import java.util.Properties;

/**
 * Simple standalone email test runner.
 * Run this class directly to test email sending without Maven compilation issues.
 */
public class SimpleEmailTest {
    
    public static void main(String[] args) {
        System.out.println("=== Email Test Runner ===");
        
        try {
            testSendRealEmail();
            testSendEmailWithComplexStackTrace();
            testSendEmailWithSpecialCharacters();
            
            System.out.println("\nAll email tests completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Email test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testSendRealEmail() throws Exception {
        System.out.println("\n--- Testing Real Email Sending ---");
        
        JavaMailSenderImpl mailSender = createMailSender();
        
        String sampleStackTrace = "java.lang.NullPointerException: This is a test exception from standalone test\n" +
                "\tat com.example.test.TestService.processData(TestService.java:45)\n" +
                "\tat com.example.test.TestController.handleRequest(TestController.java:28)\n" +
                "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\n" +
                "\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
                "\tat java.base/java.lang.reflect.Method.invoke(Method.java:568)";
        
        String emailContent = buildEmailContent(
            "Test Service", 
            "test-server-01", 
            "2025-01-15 10:30:45", 
            "HIGH",
            "POST",
            "/api/test/endpoint",
            "NullPointerException",
            "{\"test\":\"data\",\"value\":123}",
            sampleStackTrace
        );
        
        sendTestEmail(mailSender, "Test Exception Alert - Real Email", emailContent);
    }
    
    private static void testSendEmailWithComplexStackTrace() throws Exception {
        System.out.println("\n--- Testing Email with Complex Stack Trace ---");
        
        JavaMailSenderImpl mailSender = createMailSender();
        
        String complexStackTrace = "org.springframework.dao.DataIntegrityViolationException: could not execute statement; SQL [n/a]; constraint [test_constraint]\n" +
                "\tat org.springframework.orm.jpa.vendor.HibernateJpaDialect.convertHibernateAccessException(HibernateJpaDialect.java:169)\n" +
                "\tat org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:138)\n" +
                "\tat org.springframework.dao.support.ChainedPersistenceExceptionTranslator.translateExceptionIfPossible(ChainedPersistenceExceptionTranslator.java:61)\n" +
                "\tat com.example.test.TestRepository.save(TestRepository.java:42)\n" +
                "\tat com.example.test.TestService.createEntity(TestService.java:35)\n" +
                "\tat com.example.test.TestController.create(TestController.java:30)\n" +
                "\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)";
        
        String emailContent = buildEmailContent(
            "Database Service", 
            "prod-db-server-02", 
            "2025-01-15 11:45:22", 
            "CRITICAL",
            "POST",
            "/api/users/create",
            "DataIntegrityViolationException",
            "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}",
            complexStackTrace
        );
        
        sendTestEmail(mailSender, "Complex Stack Trace Test", emailContent);
    }
    
    private static void testSendEmailWithSpecialCharacters() throws Exception {
        System.out.println("\n--- Testing Email with Special Characters ---");
        
        JavaMailSenderImpl mailSender = createMailSender();
        
        String specialCharStackTrace = "java.lang.IllegalArgumentException: Invalid input containing special characters\n" +
                "\tat com.example.test.ValidationUtils.validateInput(ValidationUtils.java:25)\n" +
                "\tat com.example.test.TestService.processInput(TestService.java:38)\n" +
                "\tat com.example.test.TestController.handleInput(TestController.java:30)";
        
        String emailContent = buildEmailContent(
            "Validation Service", 
            "web-server-03", 
            "2025-01-15 12:15:33", 
            "MEDIUM",
            "POST",
            "/api/data/validate",
            "IllegalArgumentException",
            "{\"name\":\"John O'Connor\",\"email\":\"john+test@example.com\",\"description\":\"This is a test with special characters\"}",
            specialCharStackTrace
        );
        
        sendTestEmail(mailSender, "Special Characters Test", emailContent);
    }
    
    private static JavaMailSenderImpl createMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("mail.messagingengine.com");
        mailSender.setPort(587);
        mailSender.setUsername("postmaster@personifyfinancial.com");
        mailSender.setPassword("g5d8sjk66j22xq8r");
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");
        mailSender.setJavaMailProperties(props);
        
        return mailSender;
    }
    
    private static void sendTestEmail(JavaMailSenderImpl mailSender, String subject, String content) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom("postmaster@personifyfinancial.com");
        helper.setTo("test@example.com"); // Change to actual recipient for testing
        helper.setSubject(subject);
        helper.setText(content, true);
        
        System.out.println("Sending email: " + subject);
        System.out.println("From: postmaster@personifyfinancial.com");
        System.out.println("To: test@example.com");
        
        mailSender.send(message);
        System.out.println("Email sent successfully!");
    }
    
    private static String buildEmailContent(String serviceName, String server, String time, String severity, 
                                           String requestMethod, String requestURI, String exceptionName, 
                                           String inputRequest, String stackTrace) {
        StringBuilder content = new StringBuilder();
        content.append("<html><body style='font-family: Arial, sans-serif;'>");
        content.append("<h2 style='color: #d32f2f;'>Exception Alert</h2>");
        content.append("<div style='background-color: #f5f5f5; padding: 15px; border-radius: 5px;'>");
        
        content.append("<p><strong>Service Name:</strong> ").append(serviceName).append("</p>");
        content.append("<p><strong>Server:</strong> ").append(server).append("</p>");
        content.append("<p><strong>Time:</strong> ").append(time).append("</p>");
        content.append("<p><strong>Severity:</strong> <span style='color: #d32f2f;'>").append(severity).append("</span></p>");
        content.append("<p><strong>Request Method:</strong> ").append(requestMethod).append("</p>");
        content.append("<p><strong>Request URI:</strong> ").append(requestURI).append("</p>");
        content.append("<p><strong>Exception Name:</strong> <span style='color: #d32f2f;'>").append(exceptionName).append("</span></p>");
        
        if (inputRequest != null && !inputRequest.trim().isEmpty()) {
            content.append("<h3>Request Body:</h3>");
            content.append("<pre style='background-color: #e8e8e8; padding: 10px; border-radius: 3px; white-space: pre-wrap;'>")
                   .append(escapeHtml(inputRequest)).append("</pre>");
        }
        
        content.append("<h3>Stack Trace:</h3>");
        content.append("<pre style='background-color: #ffebee; padding: 10px; border-radius: 3px; white-space: pre-wrap; color: #c62828;'>")
               .append(escapeHtml(stackTrace)).append("</pre>");
        
        content.append("</div>");
        content.append("<p style='margin-top: 20px; font-size: 12px; color: #666;'>");
        content.append("This is a test exception alert from Email Test Runner.");
        content.append("</p>");
        content.append("</body></html>");
        
        return content.toString();
    }
    
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
