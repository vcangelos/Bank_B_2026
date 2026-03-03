import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DebitCard {
    
    // Card properties
    private String debitCardNumber;
    private String pin;
    private String linkedAccountId;
    private boolean isActive;
    
    public DebitCard(String debitCardNumber, String pin, String linkedAccountId) {
        this.debitCardNumber = debitCardNumber;
        this.pin = pin;
        this.linkedAccountId = linkedAccountId;
        this.isActive = true; 
    }
    
    public boolean verifyPin(String enteredPin) {
        if(!isActive) {
            System.out.println("Error: This card has been closed");
            return false;
        }
        if(this.pin.equals(enteredPin)) {
            System.out.println("PIN verified. Access Granted.");
            return true;
        } else {
            System.out.println("Incorrect PIN");
            return false;
        }
    }
    
    public String getDebitCardNumber() {
        return debitCardNumber;
    }
    
    public String getLinkedAccountId() {
        return linkedAccountId;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    // Check balance - STUB until integration with checking account team
    public double checkBalance() {
        if(!isActive) {
            System.out.println("Error: Card is not active");
            return 0.0;
        }
        
        // TODO: Integrate with checking account team
        // Will call something like: accountService.getBalance(linkedAccountId)
        System.out.println("Checking balance for account: " + linkedAccountId);
        return 1000.00; // Stub value - replace with actual integration
    }
    
    // Withdraw money - STUB until integration with checking account team
    public boolean withdraw(double amount) {
        if(!isActive) {
            System.out.println("Error: Card is not active");
            return false;
        }
        
        if(amount <= 0) {
            System.out.println("Error: Amount must be greater than 0");
            return false;
        }
        
        // TODO: Integrate with checking account team
        // Will call something like: accountService.withdraw(linkedAccountId, amount)
        System.out.println("Withdrawing $" + amount + " from account: " + linkedAccountId);
        return true; // Stub - replace with actual integration
    }
    
    // Deposit money - STUB until integration with checking account team
    public boolean deposit(double amount) {
        if(!isActive) {
            System.out.println("Error: Card is not active");
            return false;
        }
        
        if(amount <= 0) {
            System.out.println("Error: Amount must be greater than 0");
            return false;
        }
        
        // TODO: Integrate with checking account team
        // Will call something like: accountService.deposit(linkedAccountId, amount)
        System.out.println("Depositing $" + amount + " to account: " + linkedAccountId);
        return true; // Stub - replace with actual integration
    }
    
    public void closeCard() {
        this.isActive = false;
        System.out.println("Card " + debitCardNumber + " has been closed.");
    }
    
    // Main method for CSV integration
    public static void main(String[] args) {
        List<DebitCard> bankCards = new ArrayList<>();
        
        String file = "debitCard.csv"; 
        String line = "";

        System.out.println("Starting to read user data from CSV...\n");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            
            // Skip the header line
            br.readLine(); 

            // Read the file line by line
            while ((line = br.readLine()) != null) {
                
                // Split the row by commas
                String[] data = line.split(",");
                
                // Make sure we have enough data columns
                if (data.length >= 4) {
                    String userId = data[0].trim();
                    String firstName = data[1].trim();
                    String lastName = data[2].trim();
                    String debitBalance = data[3].trim();
                    
                    System.out.println("Found user: " + firstName + " " + lastName + " (Account: " + userId + ")");
                    
                    // Generate card number and PIN for each user
                    String cardNumber = "4532" + userId.replace(" ", "");
                    String pin = "1234"; // Default PIN
                    
                    DebitCard newCard = new DebitCard(cardNumber, pin, userId);
                    bankCards.add(newCard);
                    
                    System.out.println("  -> Created card: " + cardNumber + " with PIN: " + pin);
                }
            }
            
        } catch (IOException e) {
            System.out.println("Could not read the CSV file. Make sure 'debitCard.csv' is in the right folder.");
            e.printStackTrace();
        }
        
        System.out.println("\nFinished scanning the file. Created " + bankCards.size() + " debit cards.");
    }
}
// OTHER THINGS TO DO!!!!! have to implement fees and such like overdraft fees, atm fees, foreign transaction fees, monthly maintence fees and card replacement fees 
