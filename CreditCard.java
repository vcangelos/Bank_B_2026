public class CreditCard {

    // Existing instance variables
    private String cardNumber;
    private int cvv;
    private String expirationDate;
    private double balance;
    private boolean isOpen;
    private String cardType;
    private double creditLimit = 2000.0;
    private double minimumSpend = 300.0;
    private double totalSpent = 0.0;
    private double overLimitFee = 35.0;
    private boolean overLimitPenaltyApplied = false;


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

    // 🔹 Generic charge handler
    private void processPurchase(double amount) {
        if (isOpen && amount <= getAvailableCredit()) {
            balance += amount;
            totalSpent += amount;
            System.out.println("Purchase approved: $" + amount);
        } else {
            System.out.println("Purchase denied.");
        }
    }

    // 🔹 Purchase types
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

    // Add balance (kept)
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

    // 🔹 Available credit
    public double getAvailableCredit() {
        return creditLimit - balance;
    }

    // 🔹 Check minimum spend
    public void checkMinimumSpend() {
        if (totalSpent < minimumSpend) {
            isOpen = false;
            System.out.println("Account closed: minimum spend not met.");
        } else {
            System.out.println("Minimum spend requirement met.");
        }
    }

    // Monthly Spending Limit
    public boolean monthlySpendingLimit(double amount) {
        return totalSpent + amount <= creditLimit;
    }

        // Closing (existing requirement)
    public boolean closeAccount() {
        if (balance == 0) {
            isOpen = false;
            return true;
        }
        return false;
    }

  // Incur Penalties
      public void incurPenalties();
        if (totalSpent > creditLimit) {
            balance =+ overLimitFee;


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
        System.out.println("Available Credit: $" + getAvailableCredit());
        System.out.println("Account Status: " + (isOpen ? "Open" : "Closed"));
    }
}
