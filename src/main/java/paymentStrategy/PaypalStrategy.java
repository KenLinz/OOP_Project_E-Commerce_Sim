package paymentStrategy;

public class PaypalStrategy extends PaymentStrategy {
    private String email;
    private String password;
    private float balance;
    public PaypalStrategy(String email, String password, float balance) {
        this.email = email;
        this.password = password;
        this.balance = balance;
    }
    @Override
    public boolean validate() {
        if(email == null || !email.contains("@")) {
            return false;
        }
        if(password == null || password.isEmpty()){
            return false;
        }
        return true;
    }
    @Override
    public boolean processPayment(float amount){
        if (balance < amount) {
            return false;
        }
        balance -= amount;
        return true;
    }

    @Override
    public String getPaymentType(){
        return "PayPal";
    }

    public float getBalance(){
        return this.balance;
    }

}
