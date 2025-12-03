package com.commerce.ooad.E_Commerce.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import paymentStrategy.PaymentStrategy;
import paymentStrategy.VisaStrategy;
import paymentStrategy.PaypalStrategy;

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

    public PaymentMethodSQL(UserSQL user, String paymentType, BigDecimal balance,
                            String paymentEmail, String paymentPassword,
                            String paymentCardNumber, String paymentCardPin,
                            String paymentCardName) {
        this.user = user;
        this.paymentType = paymentType;
        this.balance = balance;
        this.paymentEmail = paymentEmail;
        this.paymentPassword = paymentPassword;
        this.paymentCardNumber = paymentCardNumber;
        this.paymentCardPin = paymentCardPin;
        this.paymentCardName = paymentCardName;
    }

    public boolean hasType(String type) {
        return this.paymentType.equals(type);
    }

    public boolean hasSufficientBalance(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    public void deductBalance(BigDecimal amount) {
        if (!hasSufficientBalance(amount)) {
            throw new IllegalStateException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public boolean belongsToUser(UserSQL user) {
        return this.user.getId().equals(user.getId());
    }

    public PaymentStrategy toPaymentStrategy() {
        float balanceFloat = this.balance.floatValue();
        switch (this.paymentType) {
            case "Paypal":
                return new PaypalStrategy(this.paymentEmail, this.paymentPassword, balanceFloat);
            case "Visa":
            case "MasterCard":
                return new VisaStrategy(this.paymentCardNumber, this.paymentCardPin,
                        this.paymentCardName, balanceFloat);
            default:
                throw new IllegalArgumentException("Unknown payment type: " + this.paymentType);
        }
    }

    public void updateBalanceFromStrategy(PaymentStrategy strategy) {
        this.balance = BigDecimal.valueOf(strategy.getBalance());
    }

    public Long getId() {
        return id;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getPaymentEmail() {
        return paymentEmail;
    }

    public String getPaymentCardNumber() {
        return paymentCardNumber;
    }

    public String getPaymentCardName() {
        return paymentCardName;
    }

    public UserSQL getUser() {
        return user;
    }
}