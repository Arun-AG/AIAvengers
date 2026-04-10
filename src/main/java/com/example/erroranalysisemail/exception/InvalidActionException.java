package com.example.erroranalysisemail.exception;

public class InvalidActionException extends RuntimeException {
    
    public InvalidActionException(String action) {
        super("Invalid action: " + action);
    }
    
    public InvalidActionException(String action, String message) {
        super("Invalid action '" + action + "': " + message);
    }
}
