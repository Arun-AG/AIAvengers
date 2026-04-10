package com.example.loan.model;

public class LoanRequest {
    private int income;
    private int creditScore;
    private int existingLoans;

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public int getExistingLoans() {
        return existingLoans;
    }

    public void setExistingLoans(int existingLoans) {
        this.existingLoans = existingLoans;
    }
}
