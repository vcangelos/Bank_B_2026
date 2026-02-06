public class CreditCard {

    // Instance variables (generic card info)
    private String cardNumber;
    private int cvv;
    private String expirationDate;
    private double balance;
    private boolean isOpen;
    private String cardType;

    // Constructor (Opening a credit card)
    public CreditCard(String cardNumber, int cvv, String expirationDate) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expirationDate = expirationDate;
        this.balance = 0.0;
        this.isOpen = true;
        this.cardType = determineCardType(cardNumber);
    }

    // Determines card type based on first digit
    private String determineCardType(String cardNumber) {
        char firstDigit = cardNumber.charAt(0);

        switch (firstDigit) {
            case '3':
                return "American Express";
            case '4':
                return "Visa";
            case '5':
                return "MasterCard";
            case '6':
                return "Discover";
            default:
                return "Unknown Card Type";
        }
    }

    // Add balance
    public void addCharge(double amount) {
        if (isOpen) {
            balance += amount;
        }
    }

    // Pay balance
    public void makePayment(double amount) {
        balance -= amount;
        if (balance < 0) {
            balance = 0;
        }
    }

    // Closing
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

    // Display card info
    public void displayCardInfo() {
        System.out.println("Card Type: " + cardType);
        System.out.println("Card Number: " + cardNumber);
        System.out.println("Expiration Date: " + expirationDate);
        System.out.println("Balance: $" + balance);
        System.out.println("Account Status: " + (isOpen ? "Open" : "Closed"));
    }
}
