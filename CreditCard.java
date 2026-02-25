import java.util.Random;
import java.time.LocalDate;

public class CreditCard {

    private String creditCardNumber;
    private String cardType;
    private int cvv;
    private String expirationDate;
    private double balance;
    private boolean isOpen;
    private double creditLimit = 5000.00;   // Monthly credit limit
    private double monthlySpent = 0.00;     // Tracks spending this month
    private final double PENALTY_FEE = 35.00;

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

    // Generate 4-digit CVV
    private int generateCVV() {
        Random rand = new Random();
        return 1000 + rand.nextInt(9000);
    }

    // Expiration date = 5 years from now
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

    public double getCreditLimit() {
        return creditLimit;
    }

    public double getMonthlySpent() {
        return monthlySpent;
    }

    public boolean isOpen() { return isOpen; }

    // Add charge with credit limit + penalty logic
    public void addCharge(double amount) {
        if (isOpen && amount > 0) {

            monthlySpent += amount;
            balance += amount;

            // If spending exceeds limit → apply penalty
            if (monthlySpent > creditLimit) {
                System.out.println("WARNING: Credit limit exceeded!");
                System.out.println("A penalty fee of $" + PENALTY_FEE + " has been applied.");

                balance += PENALTY_FEE;
            }

            // Round balance
            balance = Math.round(balance * 100.0) / 100.0;
        }
    }

    // Make payment
    public void makePayment(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            balance = Math.round(balance * 100.0) / 100.0;
        }
    }

    // Reset monthly spending (call once per month in real system)
    public void resetMonthlySpending() {
        monthlySpent = 0.0;
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