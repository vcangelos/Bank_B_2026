import java.util.Random;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class CreditCard {

    // Basic card information
    private String creditCardNumber;
    private String creditCardType;
    private int cvv;
    private String expirationDate;
    private double balance;
    private boolean isOpen;

    // Credit settings
    private double creditLimit = 2000.00;
    private double monthlySpent = 0.00;

    // Fees and rates
    private final double PENALTY_FEE = 35.00;
    private final double MIN_PAYMENT_RATE = 0.05;
    private final double APR = 0.20;
    private final double CASH_ADVANCE_FEE_RATE = 0.05;

    // Account tracking
    private double amountPaidThisMonth = 0.0;
    private boolean isFrozen = false;
    private int creditScore = 700;
    private int rewardPoints = 0;
    private int missedPayments = 0;

    // Transaction history
    private List<String> transactions = new ArrayList<>();

    // Constructor creates a new Visa card automatically
    public CreditCard() {
        this.creditCardNumber = generateCardNumber();
        this.creditCardType = "Visa";
        this.cvv = generateCVV();
        this.expirationDate = generateExpirationDate();
        this.balance = 0.0;
        this.isOpen = true;
    }

    // Generates a random Visa card number
    private String generateCardNumber() {
        Random rand = new Random();
        String number = "4";

        while (number.length() < 16) {
            number += rand.nextInt(10);
        }

        return number;
    }

    // Generates a random 3-digit CVV
    private int generateCVV() {
        Random rand = new Random();
        return 100 + rand.nextInt(900);
    }

    // Sets expiration date to 5 years from today
    private String generateExpirationDate() {
        LocalDate futureDate = LocalDate.now().plusYears(5);
        int month = futureDate.getMonthValue();
        int year = futureDate.getYear() % 100;
        return String.format("%02d/%02d", month, year);
    }

    // Helper method for consistent money rounding
    private double roundMoney(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }

    // Keeps credit score realistic
    private void clampCreditScore() {
        if (creditScore > 850) {
            creditScore = 850;
        }
        if (creditScore < 300) {
            creditScore = 300;
        }
    }

    // Getter methods
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public String getFormattedCardNumber() {
        return creditCardNumber.substring(0, 4) + " " +
                creditCardNumber.substring(4, 8) + " " +
                creditCardNumber.substring(8, 12) + " " +
                creditCardNumber.substring(12, 16);
    }

    public String getCreditCardType() {
        return creditCardType;
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

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public double getAvailableCredit() {
        return roundMoney(creditLimit - balance);
    }

    public int getCreditScore() {
        return creditScore;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public int getMissedPayments() {
        return missedPayments;
    }

    public String getCardStatus() {
        if (!isOpen) {
            return "Closed";
        }
        if (isFrozen) {
            return "Frozen";
        }
        return "Active";
    }

    // Displays basic credit card details
    public void display() {
        System.out.println("Card Type: " + creditCardType);
        System.out.println("Card Number: " + getFormattedCardNumber());
        System.out.println("CVV: " + cvv);
        System.out.println("Expiration Date: " + expirationDate);
        System.out.printf("Balance: $%.2f%n", balance);
        System.out.printf("Credit Limit: $%.2f%n", creditLimit);
        System.out.printf("Monthly Spent: $%.2f%n", monthlySpent);
        System.out.printf("Available Credit: $%.2f%n", getAvailableCredit());
        System.out.println("Credit Score: " + creditScore);
        System.out.println("Reward Points: " + rewardPoints);
        System.out.println("Missed Payments: " + missedPayments);
        System.out.println("Card Status: " + getCardStatus());
    }

    // Detects suspicious purchases and freezes card
    private void detectFraud(double amount) {
        if (amount >= 1000.00) {
            System.out.println("Suspicious transaction detected.");
            System.out.println("Card frozen for security.");
            freezeCard();

            transactions.add("Fraud Alert: Attempted charge $" +
                    String.format("%.2f", amount) +
                    " on " + LocalDate.now());
        }
    }

    // Adds a normal purchase to the card
    public void addCharge(double amount) {
        if (isFrozen) {
            System.out.println("Card is frozen.");
            return;
        }

        if (!isOpen) {
            System.out.println("Card is closed.");
            return;
        }

        if (amount <= 0) {
            System.out.println("Invalid charge.");
            return;
        }

        detectFraud(amount);

        if (isFrozen) {
            return;
        }

        if (balance + amount > creditLimit) {
            System.out.println("Transaction declined. Over credit limit.");
            transactions.add("Declined Charge $" +
                    String.format("%.2f", amount) +
                    " on " + LocalDate.now());
            return;
        }

        monthlySpent = roundMoney(monthlySpent + amount);
        balance = roundMoney(balance + amount);
        rewardPoints += (int) amount;

        transactions.add("Charge $" +
                String.format("%.2f", amount) +
                " on " + LocalDate.now());
    }

    // Adds a purchase with a category label
    public void addChargeWithCategory(double amount, String category) {
        if (category == null || category.trim().isEmpty()) {
            category = "General";
        }

        if (isFrozen) {
            System.out.println("Card is frozen.");
            return;
        }

        if (!isOpen) {
            System.out.println("Card is closed.");
            return;
        }

        if (amount <= 0) {
            System.out.println("Invalid charge.");
            return;
        }

        detectFraud(amount);

        if (isFrozen) {
            return;
        }

        if (balance + amount > creditLimit) {
            System.out.println("Transaction declined. Over credit limit.");
            transactions.add("Declined " + category + " purchase $" +
                    String.format("%.2f", amount) +
                    " on " + LocalDate.now());
            return;
        }

        monthlySpent = roundMoney(monthlySpent + amount);
        balance = roundMoney(balance + amount);
        rewardPoints += (int) amount;

        transactions.add(category + " purchase $" +
                String.format("%.2f", amount) +
                " on " + LocalDate.now());
    }

    // Adds a purchase with merchant name
    public void addChargeWithMerchant(double amount, String merchant) {
        if (merchant == null || merchant.trim().isEmpty()) {
            merchant = "Unknown";
        }

        if (isFrozen) {
            System.out.println("Card is frozen.");
            return;
        }

        if (!isOpen) {
            System.out.println("Card is closed.");
            return;
        }

        if (amount <= 0) {
            System.out.println("Invalid charge.");
            return;
        }

        detectFraud(amount);

        if (isFrozen) {
            return;
        }

        if (balance + amount > creditLimit) {
            System.out.println("Transaction declined. Over credit limit.");
            transactions.add("Declined purchase at " + merchant + " $" +
                    String.format("%.2f", amount) +
                    " on " + LocalDate.now());
            return;
        }

        monthlySpent = roundMoney(monthlySpent + amount);
        balance = roundMoney(balance + amount);
        rewardPoints += (int) amount;

        transactions.add("Purchase at " + merchant + " $" +
                String.format("%.2f", amount) +
                " on " + LocalDate.now());
    }

    // Processes a payment
    public void makePayment(double amount) {
        if (!isOpen) {
            System.out.println("Card is closed.");
            return;
        }

        if (amount <= 0) {
            System.out.println("Invalid payment.");
            return;
        }

        if (amount > balance) {
            System.out.println("Payment exceeds current balance.");
            return;
        }

        balance = roundMoney(balance - amount);
        amountPaidThisMonth = roundMoney(amountPaidThisMonth + amount);

        creditScore += 5;
        clampCreditScore();

        transactions.add("Payment $" +
                String.format("%.2f", amount) +
                " on " + LocalDate.now());
    }

    // Allows ATM cash advance with fee
    public void cashAdvance(double amount) {
        if (isFrozen) {
            System.out.println("Card is frozen. Cash advance blocked.");
            return;
        }

        if (!isOpen) {
            System.out.println("Card is closed.");
            return;
        }

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        double fee = roundMoney(amount * CASH_ADVANCE_FEE_RATE);
        double total = roundMoney(amount + fee);

        if (balance + total > creditLimit) {
            System.out.println("Cash advance declined.");
            transactions.add("Declined Cash Advance $" +
                    String.format("%.2f", amount) +
                    " on " + LocalDate.now());
            return;
        }

        balance = roundMoney(balance + total);
        monthlySpent = roundMoney(monthlySpent + amount);

        transactions.add("Cash Advance $" +
                String.format("%.2f", amount) +
                " on " + LocalDate.now());
        transactions.add("Cash Advance Fee $" +
                String.format("%.2f", fee) +
                " on " + LocalDate.now());
    }

    // Redeems reward points
    public void redeemRewards(int points) {
        if (points <= 0 || points > rewardPoints) {
            System.out.println("Invalid reward redemption.");
            return;
        }

        rewardPoints -= points;

        transactions.add("Rewards Redeemed " +
                points +
                " points on " + LocalDate.now());

        System.out.println("Rewards redeemed.");
    }

    // Requests credit limit increase based on credit score
    public void requestCreditLimitIncrease(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid increase amount.");
            return;
        }

        if (creditScore >= 720) {
            creditLimit = roundMoney(creditLimit + amount);
            System.out.println("Credit limit increased.");

            transactions.add("Credit Limit Increased by $" +
                    String.format("%.2f", amount) +
                    " on " + LocalDate.now());
        } else {
            System.out.println("Credit limit request denied.");

            transactions.add("Credit Limit Increase Denied on " + LocalDate.now());
        }
    }

    // Replaces the card if lost or stolen
    public void replaceCard() {
        if (!isOpen) {
            System.out.println("Closed card cannot be replaced.");
            return;
        }

        String oldNumber = creditCardNumber;

        creditCardNumber = generateCardNumber();
        cvv = generateCVV();
        expirationDate = generateExpirationDate();
        isFrozen = false;

        transactions.add("Card replaced on " + LocalDate.now() +
                " old ending: " + oldNumber.substring(12));
    }

    // Calculates minimum payment
    public double calculateMinimumPayment() {
        return roundMoney(balance * MIN_PAYMENT_RATE);
    }

    // Runs monthly billing logic
    public void endBillingCycle() {
        double min = calculateMinimumPayment();

        if (balance > 0 && amountPaidThisMonth < min) {
            System.out.println("Minimum payment not met.");

            balance = roundMoney(balance + PENALTY_FEE);
            creditScore -= 30;
            missedPayments++;
            clampCreditScore();

            transactions.add("Late Fee $" +
                    String.format("%.2f", PENALTY_FEE) +
                    " on " + LocalDate.now());
        } else if (balance > 0) {
            System.out.println("Minimum payment satisfied.");
            creditScore += 10;
            clampCreditScore();
        }

        double interest = roundMoney((APR / 12) * balance);
        balance = roundMoney(balance + interest);

        transactions.add("Interest $" +
                String.format("%.2f", interest) +
                " on " + LocalDate.now());

        amountPaidThisMonth = 0.0;
        monthlySpent = 0.0;
    }

    // Prints a simple monthly statement
    public void printMonthlyStatement() {
        System.out.println("\nVisa Monthly Statement");
        System.out.println("Card Number: " + getFormattedCardNumber());
        System.out.println("Expiration Date: " + expirationDate);
        System.out.println("Card Status: " + getCardStatus());
        System.out.printf("Current Balance: $%.2f%n", balance);
        System.out.printf("Available Credit: $%.2f%n", getAvailableCredit());
        System.out.printf("Minimum Payment Due: $%.2f%n", calculateMinimumPayment());
        System.out.println("Credit Score: " + creditScore);
        System.out.println("Reward Points: " + rewardPoints);
        System.out.println("Missed Payments: " + missedPayments);
        System.out.println("\nRecent Transactions:");
        printTransactionHistory();
    }

    // Verifies card number and CVV
    public boolean validateCard(String number, int enteredCVV) {
        return this.creditCardNumber.equals(number) && this.cvv == enteredCVV;
    }

    // Freezes card
    public void freezeCard() {
        isFrozen = true;
        System.out.println("Card frozen.");
    }

    // Unfreezes card
    public void unfreezeCard() {
        isFrozen = false;
        System.out.println("Card unfrozen.");
    }

    // Prints transaction history
    public void printTransactionHistory() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions.");
            return;
        }

        for (String t : transactions) {
            System.out.println(t);
        }
    }

    // Card can close only if balance is zero
    public boolean closeCard() {
        if (balance == 0) {
            isOpen = false;
            return true;
        }

        return false;
    }

    // Converts card data into one CSV line
    public String toCSV() {
        return creditCardNumber + "," +
                creditCardType + "," +
                cvv + "," +
                expirationDate + "," +
                balance + "," +
                creditLimit + "," +
                monthlySpent + "," +
                amountPaidThisMonth + "," +
                isFrozen + "," +
                creditScore + "," +
                rewardPoints + "," +
                missedPayments + "," +
                isOpen;
    }

    // Saves or updates the card in a CSV file
    public void saveOrUpdateCSV(String fileName) {
        List<String> lines = new ArrayList<>();
        boolean cardFound = false;

        try {
            File file = new File(fileName);

            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("CardNumber")) {
                        lines.add(line);
                        continue;
                    }

                    String[] parts = line.split(",");

                    if (parts.length > 0 && parts[0].equals(this.creditCardNumber)) {
                        lines.add(this.toCSV());
                        cardFound = true;
                    } else {
                        lines.add(line);
                    }
                }

                reader.close();
            }

            if (!cardFound) {
                if (!file.exists()) {
                    lines.add("CardNumber,CardType,CVV,ExpirationDate,Balance,CreditLimit,MonthlySpent,AmountPaidThisMonth,IsFrozen,CreditScore,RewardPoints,MissedPayments,IsOpen");
                }

                lines.add(this.toCSV());
            }

            PrintWriter writer = new PrintWriter(new FileWriter(fileName));
            for (String l : lines) {
                writer.println(l);
            }
            writer.close();

            System.out.println("Card data saved.");
        } catch (IOException e) {
            System.out.println("Error saving to CSV.");
        }
    }
}