package it.ispwproject.nightflow.bean;

public class PaymentRequestBean {
    private double amount;
    private String userEmail;

    public PaymentRequestBean(double amount, String userEmail) {
        this.amount = amount;
        this.userEmail = userEmail;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}