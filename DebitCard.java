// DebitCard.java

import java.util.List;
import java.util.ArrayList;

public class DebitCard {

    private String cardNumber;
    private String cardHolder;
    private String expiryDate;
    private User user;  // Changed to use User class instead

    public DebitCard(User user, String cardNumber, String cardHolder, String expiryDate) {
        this.user = user;
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.expiryDate = expiryDate;
    }

    // Other getters and setters remain unchanged

    // New method to handle CSV operations for DebitCard
    public static List<DebitCard> loadDebitCardsFromCSV(String filePath) {
        List<DebitCard> debitCards = new ArrayList<>();
        // Logic to read from CSV and create DebitCard instances using User class details
        // Example: Read the CSV line, construct a User object, create DebitCard
        return debitCards;
    }

    public void saveToCSV(String filePath) {
        // Logic to save this DebitCard instance to CSV file
        // This would involve writing user details as well
    }
    
    // Other methods as needed
}