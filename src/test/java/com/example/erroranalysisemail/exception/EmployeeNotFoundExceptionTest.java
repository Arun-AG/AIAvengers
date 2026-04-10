package com.example.erroranalysisemail.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeNotFoundExceptionTest {

    @Test
    void testConstructorWithId() {
        EmployeeNotFoundException exception = new EmployeeNotFoundException(123L);
        assertEquals("Employee not found with id: 123", exception.getMessage());
    }

    @Test
    void testConstructorWithMessage() {
        String customMessage = "Custom error message";
        EmployeeNotFoundException exception = new EmployeeNotFoundException(customMessage);
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void testIsRuntimeException() {
        EmployeeNotFoundException exception = new EmployeeNotFoundException(1L);
        assertTrue(exception instanceof RuntimeException);
    }
}
