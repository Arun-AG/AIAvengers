package com.example.erroranalysisemail.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
