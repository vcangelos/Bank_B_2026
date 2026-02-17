public class DebitCard {
    // Card properties
    private String cardNumber;
    private String pin;
    private String linkedAccountId;
    private boolean isActive;
    
    public DebitCard(String cardNumber, String pin, String linkedAccountId) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.linkedAccountId = linkedAccountId;
        this.isActive = true; 
    }
    
    public boolean verifyPin(String enteredPin){
        if(!isActive){
            System.out.println("Error : This card has been closed");
            return false;
        }
        if(this.pin.equals(enteredPin)){
            System.out.println("PIN verified. Access Granted.");
            return true;
        }else {
            System.out.println("Incorrect PIN");
            return false;
        }
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    
    public String getLinkedAccountId() {
        return linkedAccountId;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public double checkBalance() {
        if(!isActive) {
            System.out.println("Error: Card is not active");
            return 0.0;
        }
        System.out.println("Checking balance for account: " + linkedAccountId);
        return 1000.00; //temporary
    }
    
    public boolean withdraw(double amount) {
        if(!isActive) {
            System.out.println("Error: Card is not active");
            return false;
        }
        System.out.println("Withdrawing $" + amount + " from account: " + linkedAccountId);
        return true;
        //replace this with code that calls the checking account team's withdraw method to actually remove money.
        //later situation
    }
    
    public boolean deposit(double amount) {
        if(!isActive) {
            System.out.println("Error: Card is not active");
            return false;
        }
        System.out.println("Depositing $" + amount + " to account: " + linkedAccountId);
        return true;
    }
    
    public void closeCard() {
        this.isActive = false;
        System.out.println("Card " + cardNumber + " has been closed.");
    }
    
    public static void main(String[] args) {
        // optional: add demo code here to test later during standup
    }

}
