package com.example.erroranalysisemail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ErrorAnalysisEmailApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErrorAnalysisEmailApplication.class, args);
    }

}
