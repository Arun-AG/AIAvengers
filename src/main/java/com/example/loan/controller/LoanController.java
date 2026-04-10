package com.example.loan.controller;

import com.example.loan.model.LoanRequest;
import com.example.loan.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @GetMapping("/")
    public String home() {
        return "Loan API Running";
    }

    @PostMapping("/loan-check")
    public Map<String, Object> checkLoan(@RequestBody LoanRequest request) {
        return loanService.checkEligibility(request);
    }

    @GetMapping("/error")
    public String error() {
        throw new RuntimeException("Default system failure");
    }
}
