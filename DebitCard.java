import java.util.Scanner;

public class DebitCard {
    
    // to do: connect to account team's system later
    private String cardNumber;
    private String pin;
    private String accountId;  // links to other team's account
    
    public DebitCard(String cardNumber, String pin, String accountId) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.accountId = accountId;
    }
    
    }
}