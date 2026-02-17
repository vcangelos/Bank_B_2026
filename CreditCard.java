import java.util.Random;

public class CreditCard {

    private String cardNumber;
    private int cvv;
    private String expirationDate;
    private double balance;
    private boolean isOpen;

    // Constructor – auto generates all card info
    public CreditCard() {
        this.cardNumber = generateCardNumber();
        this.cvv = generateCVV();
        this.expirationDate = generateExpirationDate();
        this.balance = 0.0;
        this.isOpen = true;
    }

    // Generate 16-digit card number
    private String generateCardNumber() {
        Random rand = new Random();
        String number = "";
        for (int i = 0; i < 16; i++) {
            number += rand.nextInt(10);
        }
        return number;
    }

    // Generate 3-digit CVV
    private int generateCVV() {
        Random rand = new Random();
        return 100 + rand.nextInt(900); // ensures 100–999
    }

    // Generate expiration date (MM/YY)
    private String generateExpirationDate() {
        Random rand = new Random();

        int month = 1 + rand.nextInt(12);
        int currentYear = 26;
        int yearOffset = 2 + rand.nextInt(2); // 2 or 3 years

        int year = currentYear + yearOffset;

        return String.format("%02d/%d", month, year);
    }


    public String getCardNumber() {
        return cardNumber;
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

    public void addCharge(double amount) {
        if (isOpen && amount > 0) {
            balance += amount;
        }
    }

    public void makePayment(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
        }
    }

    // Card can only close if balance is 0
    public boolean closeCard() {
        if (balance == 0) {
            isOpen = false;
            return true;
        }
        return false;
    }
}
