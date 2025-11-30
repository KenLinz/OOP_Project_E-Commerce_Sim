package com.commerce.ooad.E_Commerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payment_methods")
public class PaymentMethodSQL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_type", nullable = false)
    private String paymentType;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "payment_email")
    private String paymentEmail;

    @Column(name = "payment_password")
    private String paymentPassword;

    @Column(name = "payment_card_number")
    private String paymentCardNumber;

    @Column(name = "payment_card_pin")
    private String paymentCardPin;

    @Column(name = "payment_card_name")
    private String paymentCardName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private UserSQL user;

    public PaymentMethodSQL() {}

    public PaymentMethodSQL(UserSQL user, String paymentType, BigDecimal balance, String paymentEmail, String paymentPassword, String paymentCardNumber, String paymentCardPin, String paymentCardName) {
        this.user = user;
        this.paymentType = paymentType;
        this.balance = balance;
        this.paymentEmail = paymentEmail;
        this.paymentPassword = paymentPassword;
        this.paymentCardNumber = paymentCardNumber;
        this.paymentCardPin = paymentCardPin;
        this.paymentCardName = paymentCardName;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getPaymentEmail() {
        return paymentEmail;
    }

    public void setPaymentEmail(String paymentEmail) {
        this.paymentEmail = paymentEmail;
    }

    public String getPaymentPassword() {
        return paymentPassword;
    }

    public void setPaymentPassword(String paymentPassword) {
        this.paymentPassword = paymentPassword;
    }

    public String getPaymentCardNumber() {
        return paymentCardNumber;
    }

    public void setPaymentCardNumber(String paymentCardNumber) {
        this.paymentCardNumber = paymentCardNumber;
    }

    public String getPaymentCardPin() {
        return paymentCardPin;
    }

    public void setPaymentCardPin(String paymentCardPin) {
        this.paymentCardPin = paymentCardPin;
    }

    public String getPaymentCardName() {
        return paymentCardName;
    }

    public void setPaymentCardName(String paymentCardName) {
        this.paymentCardName = paymentCardName;
    }

    public UserSQL getUser() {
        return user;
    }

    public void setUser(UserSQL user) {
        this.user = user;
    }
}