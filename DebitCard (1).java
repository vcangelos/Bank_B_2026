import java.util.Scanner;

public class DebitCard {
    public static void main(String[] args) {
    }
    // to do: connect to account team's system later
    private String cardNumber;
    private String pin;     // links to other team's account
    private boolean isActive
    
    private String linkedAccountId;
    
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
    public void 
    
    }
