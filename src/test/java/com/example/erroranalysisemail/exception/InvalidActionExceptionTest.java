package com.example.erroranalysisemail.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InvalidActionExceptionTest {

    @Test
    void testConstructorWithAction() {
        InvalidActionException exception = new InvalidActionException("INVALID");
        assertEquals("Invalid action: INVALID", exception.getMessage());
    }

    @Test
    void testConstructorWithActionAndMessage() {
        InvalidActionException exception = new InvalidActionException("INVALID", "This action is not supported");
        assertEquals("Invalid action 'INVALID': This action is not supported", exception.getMessage());
    }

    @Test
    void testIsRuntimeException() {
        InvalidActionException exception = new InvalidActionException("TEST");
        assertTrue(exception instanceof RuntimeException);
    }
}
