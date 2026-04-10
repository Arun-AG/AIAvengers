package com.example.loan.service;

import com.example.loan.model.LoanRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoanService {

    public Map<String, Object> checkEligibility(LoanRequest req) {

        if (req.getIncome() <= 0)
            throw new RuntimeException("Invalid income");

        if (req.getCreditScore() <= 0)
            throw new RuntimeException("Invalid credit score");

        if (req.getIncome() < 10000)
            throw new RuntimeException("Income too low");

        if (req.getCreditScore() < 600)
            throw new RuntimeException("Poor credit score");

        if (req.getExistingLoans() > 3)
            throw new RuntimeException("Too many active loans");

        Map<String, Object> response = new HashMap<>();
        response.put("status", "Approved");
        response.put("eligibleLoanAmount", req.getIncome() * 5);

        return response;
    }
}
