import java.util.Random;
import java.time.LocalDate;

public class CreditCard {

    private String creditCardNumber;
    private String cardType;
    private int cvv;  // 4-digit CVV for AMEX
    private String expirationDate;
    private double balance;
    private boolean isOpen;


    // Constructor – auto generates AMEX card
    public CreditCard() {
        this.creditCardNumber = generateCardNumber();
        this.cardType = "American Express";
        this.cvv = generateCVV();
        this.expirationDate = generateExpirationDate();
        this.balance = 0.0;
        this.isOpen = true;
    }

    // Generate AMEX card number (15 digits, starts with 34 or 37)
    private String generateCardNumber() {
        Random rand = new Random();

        String[] prefixes = {"34", "37"};
        String prefix = prefixes[rand.nextInt(prefixes.length)];

        String number = prefix;

        while (number.length() < 15) {
            number += rand.nextInt(10);
        }

        return number;
    }

    // Generate 4-digit CVV (1000–9999)
    private int generateCVV() {
        Random rand = new Random();
        return 1000 + rand.nextInt(9000);
    }

    // Expiration date = 5 years from current date
    private String generateExpirationDate() {
        LocalDate futureDate = LocalDate.now().plusYears(5);

        int month = futureDate.getMonthValue();
        int year = futureDate.getYear() % 100;

        return String.format("%02d/%02d", month, year);
    }

    // GETTERS
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public int getCVV() {
        return cvv;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public double getBalance() {
        return balance;
    }

    public boolean isOpen() {
        return isOpen;
    }

    // Add charge (2 decimal precision)
    public void addCharge(double amount) {
        if (isOpen && amount > 0) {
            balance = Math.round((balance + amount) * 100.0) / 100.0;
        }
    }

    // Make payment
    public void makePayment(double amount) {
        if (amount > 0 && amount <= balance) {
            balance = Math.round((balance - amount) * 100.0) / 100.0;
        }
    }

    // Close only if balance is 0
    public boolean closeCard() {
        if (balance == 0.0) {
            isOpen = false;
            return true;
        }
        return false;
    }
}
