package paymentStrategy;

public abstract class PaymentStrategy {
    public abstract boolean validate();
    public abstract boolean processPayment(float amount);
    public abstract String getPaymentType();
    public abstract float getBalance();
}