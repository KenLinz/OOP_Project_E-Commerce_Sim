package paymentStrategy;

public class VisaStrategy extends PaymentStrategy {
    private String cardNumber;
    private String cardPin;
    private String cardName;
    private float balance;
    public VisaStrategy(String cardNumber, String cardPin, String cardName, float balance){
        this.cardNumber = cardNumber;
        this.cardPin = cardPin;
        this.cardName = cardName;
        this.balance = balance;
    } 
    @Override
    public boolean validate() {
        if(cardNumber == null || cardNumber.length() != 16) {
            return false;
        }
        if(cardPin == null || cardPin.length() != 4) {
            return false;
        }
        if(cardName == null || cardName.isEmpty()) {
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
        return "Visa";
    }

    public float getBalance(){
        return this.balance;
    }

}
