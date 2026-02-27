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
    
    public double checkBalance() {
        if(!isActive) {
            System.out.println("Error: Card is not active");
            return 0.0;
        }
        System.out.println("Checking balance for account: " + linkedAccountId);
        return 1000.00; // temporary until linked with accounts team
    }
    
    public boolean withdraw(double amount) {
        if(!isActive) {
            System.out.println("Error: Card is not active");
            return false;
        }
        System.out.println("Withdrawing $" + amount + " from account: " + linkedAccountId);
        return true;
        // replace this with code that calls the checking account team's withdraw method to actually remove money.
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
        System.out.println("Card " + debitCardNumber + " has been closed.");
    }
    
    public static void main(String[] args) {
        // This list will hold all the debit cards once we have the full data
        List<DebitCard> bankCards = new ArrayList<>();
        
        String file = "debitCard.csv"; 
        String line = "";

        System.out.println("Starting to read user data from CSV...\n");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            
            // Read and skip the first line (the headers)
            br.readLine(); 

            // Read the file line by line until there's nothing left
            while ((line = br.readLine()) != null) {
                
                // Split the row by commas
                String[] data = line.split(",");
                
                // Make sure we have enough data columns to avoid errors
                if (data.length >= 4) {
                    String userId = data[0].trim();
                    String firstName = data[1].trim();
                    String lastName = data[2].trim();
                    
                    System.out.println("Found user: " + firstName + " " + lastName + " (Account: " + userId + ")");
                    
                    // TODO: We need a Card Number and PIN to actually build the DebitCard object.
                    // Once the team decides if we are adding these to the CSV or generating them
                    // dynamically in the code, uncomment the lines below and plug the real variables in.
                    
                    // String realCardNumber = ???
                    // String realPin = ???
                    // DebitCard newCard = new DebitCard(realCardNumber, realPin, userId);
                    // bankCards.add(newCard);
                }
            }
            
        } catch (IOException e) {
            System.out.println("Could not read the CSV file. Make sure 'debitCard.csv' is in the right folder.");
            e.printStackTrace();
        }
        
        System.out.println("\nFinished scanning the file. Waiting on Card/PIN logic to actually build the objects.");
    }
}
