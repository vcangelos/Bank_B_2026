import java.util.Random;
import java.time.LocalDate;

public class CreditCard {


    //Notes
    // Will add random import to generate CardNumber and CVV
    // Will implement local date import to automatically read expiration date


    // Instance Variables
    private String cardNumber;
    private int cvv;
    private String expirationDate;
    private double balance;
    private boolean isOpen;
    private String cardType;
    private double creditScore;
    private double creditLimit = 2000.0;
    private double minimumSpend = 300.0;
    private double totalSpent = 0.0;
    private double overLimitFee = 35.0;
    private boolean overLimitPenaltyApplied = false;


    // Constructor
    public CreditCard() {

        Random rand = new Random();

        // Generate 16-digit card number
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            number.append(rand.nextInt(10)); // digits 0–9
        }
        this.cardNumber = number.toString();

        // Generate 3-digit CVV (100–999)
        this.cvv = 100 + rand.nextInt(900);

        // Generate expiration date (3 years from now)
        LocalDate futureDate = LocalDate.now().plusYears(3);
        int month = futureDate.getMonthValue();
        int year = futureDate.getYear() % 100;
        this.expirationDate = String.format("%02d/%02d", month, year);
        this.balance = 0.0;
        this.isOpen = true;
        this.cardType = determineCardType(cardNumber);
    }



    // Determines card type using if-else
    private String determineCardType(String cardNumber) {
        char firstDigit = cardNumber.charAt(0);

        if (firstDigit == '3') {
            return "American Express";
        }
        else if (firstDigit == '4') {
            return "Visa";
        }
        else if (firstDigit == '5') {
            return "MasterCard";
        }
        else if (firstDigit == '6') {
            return "Discover";
        }
        else {
            return "Unknown Card Type";
        }
    }



    // Purchase Processing
    private void processPurchase(double amount) {

        if (!isOpen) {
            System.out.println("Purchase denied. Account is closed.");
            return;
        }

        if (!monthlySpendingLimit(amount)) {
            System.out.println("Purchase denied. Monthly limit exceeded.");
            incurPenalties();
            return;
        }

        if (amount <= getAvailableCredit()) {
            balance += amount;
            totalSpent += amount;
            System.out.println("Purchase approved: $" + amount);
        } else {
            System.out.println("Purchase denied. Not enough available credit.");
        }
    }


    // Purchase Methods
    public void swipe(double amount) {
        processPurchase(amount);
    }

    public void tap(double amount) {
        processPurchase(amount);
    }

    public void digitalPay(double amount) {
        processPurchase(amount);
    }

    public void onlinePurchase(double amount) {
        processPurchase(amount);
    }


    // Payments
    public void makePayment(double amount) {
        balance -= amount;

        if (balance < 0) {
            balance = 0;
        }

        System.out.println("Payment made: $" + amount);
    }


    // Credit & Limits
    public double getAvailableCredit() {
        return creditLimit - balance;
    }

    public boolean monthlySpendingLimit(double amount) {
        return totalSpent + amount <= creditLimit;
    }


    // Minimum Spend Check
    public void checkMinimumSpend() {
        if (totalSpent < minimumSpend) {
            isOpen = false;
            System.out.println("Account closed: minimum spend not met.");
        } else {
            System.out.println("Minimum spend requirement met.");
        }
    }


    // Penalties
    public void incurPenalties() {
        if (!overLimitPenaltyApplied) {
            balance += overLimitFee;
            overLimitPenaltyApplied = true;
            System.out.println("Over-limit fee applied: $" + overLimitFee);
        }
    }


    // Close Account
    public boolean closeAccount() {
        if (balance == 0) {
            isOpen = false;
            return true;
        }
        return false;
    }


    // Getters
    public double getBalance() {
        return balance;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public String getCardType() {
        return cardType;
    }


    // Display Info
    public void displayCardInfo() {
        System.out.println("Card Type: " + cardType);
        System.out.println("Card Number: " + cardNumber);
        System.out.println("Expiration Date: " + expirationDate);
        System.out.println("Balance: $" + balance);
        System.out.println("Available Credit: $" + getAvailableCredit());
        System.out.println("Account Status: " + (isOpen ? "Open" : "Closed"));
    }
}
