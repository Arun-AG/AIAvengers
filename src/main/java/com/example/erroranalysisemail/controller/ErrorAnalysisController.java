package com.example.erroranalysisemail.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ErrorAnalysisController {

    @GetMapping("/health")
    public String health() {
        return "Error Analysis Email Application is running!";
    }

    @GetMapping("/test")
    public String test() {
        return "Test endpoint working successfully!";
    }

    @GetMapping("/test-exception")
    public String testException(@RequestParam(required = false) String type) {
        if ("runtime".equals(type)) {
            throw new RuntimeException("This is a test runtime exception");
        } else if ("illegal".equals(type)) {
            throw new IllegalArgumentException("This is a test illegal argument exception");
        } else if ("null".equals(type)) {
            String nullString = null;
            return nullString.toString();
        } else {
            throw new Exception("This is a test general exception");
        }
    }
}
