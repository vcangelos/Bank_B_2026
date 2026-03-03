import java.util.Random;
import java.time.LocalDate;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CreditCard {

    private String creditCardNumber;
    private String cardType;
    private int cvv;
    private String expirationDate;
    private double balance;
    private boolean isOpen;
    private double creditLimit = 2000.00;
    private double monthlySpent = 0.00;
    private final double PENALTY_FEE = 35.00;
    private final double MIN_PAYMENT_RATE = 0.05;
    private double amountPaidThisMonth = 0.0;

    // Constructor
    public CreditCard() {
        this.creditCardNumber = generateCardNumber();
        this.cardType = "Visa"; // Changed from American Express to Visa
        this.cvv = generateCVV();
        this.expirationDate = generateExpirationDate();
        this.balance = 0.0;
        this.isOpen = true;
    }

    // =========================
    // CARD GENERATION METHODS
    // =========================

    private String generateCardNumber() {
        Random rand = new Random();
        String number = "4"; // Visa cards start with 4

        while (number.length() < 16) { // Visa has 16 digits
            number += rand.nextInt(10);
        }

        return number;
    }

    private int generateCVV() {
        Random rand = new Random();
        return 100 + rand.nextInt(900); // Visa uses 3-digit CVV
    }

    private String generateExpirationDate() {
        LocalDate futureDate = LocalDate.now().plusYears(5);
        int month = futureDate.getMonthValue();
        int year = futureDate.getYear() % 100;
        return String.format("%02d/%02d", month, year);
    }

    // =========================
    // GETTERS
    // =========================

    public String getCreditCardNumber() { return creditCardNumber; }
    public String getCardType() { return cardType; }
    public int getCVV() { return cvv; }
    public String getExpirationDate() { return expirationDate; }
    public double getBalance() { return balance; }
    public double getCreditLimit() { return creditLimit; }
    public double getMonthlySpent() { return monthlySpent; }
    public boolean isOpen() { return isOpen; }

    // =========================
    // TRANSACTION METHODS
    // =========================

    public void addCharge(double amount) {
        if (isOpen && amount > 0) {

            monthlySpent += amount;
            balance += amount;

            if (monthlySpent > creditLimit) {
                System.out.println("WARNING: Credit limit exceeded!");
                System.out.println("Penalty fee of $" + PENALTY_FEE + " applied.");
                balance += PENALTY_FEE;
            }

            balance = Math.round(balance * 100.0) / 100.0;
        }
    }

    public void makePayment(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            amountPaidThisMonth += amount;
            balance = Math.round(balance * 100.0) / 100.0;
        }
    }

    public double calculateMinimumPayment() {
        double minPayment = balance * MIN_PAYMENT_RATE;
        return Math.round(minPayment * 100.0) / 100.0;
    }

    public void endBillingCycle() {
        double requiredMin = calculateMinimumPayment();

        if (balance > 0 && amountPaidThisMonth < requiredMin) {
            System.out.println("Minimum Payment not met!");
            balance += PENALTY_FEE;
        } else {
            System.out.println("Minimum payment satisfied.");
        }

        balance = Math.round(balance * 100.0) / 100.0;
        amountPaidThisMonth = 0.0;
        monthlySpent = 0.0;
    }

    public boolean closeCard() {
        if (balance == 0.0) {
            isOpen = false;
            return true;
        }
        return false;
    }

    // =========================
    // CSV STORAGE METHODS
    // =========================

    public String toCSV() {
        return creditCardNumber + "," +
                cardType + "," +
                cvv + "," +
                expirationDate + "," +
                balance + "," +
                creditLimit + "," +
                monthlySpent + "," +
                amountPaidThisMonth + "," +
                isOpen;
    }

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

                    if (parts[0].equals(this.creditCardNumber)) {
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
                    lines.add("CardNumber,CardType,CVV,ExpirationDate,Balance,CreditLimit,MonthlySpent,AmountPaidThisMonth,IsOpen");
                }

                lines.add(this.toCSV());
            }

            PrintWriter writer = new PrintWriter(new FileWriter(fileName));
            for (String l : lines) {
                writer.println(l);
            }
            writer.close();

            System.out.println("Card data saved/updated successfully.");

        } catch (IOException e) {
            System.out.println("Error saving to CSV.");
            e.printStackTrace();
        }
    }
}