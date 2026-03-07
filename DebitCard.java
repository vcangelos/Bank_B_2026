import java.time.LocalDate;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Map;
import java.util.List;
import java.util.Random;
import java.io.IOException;

public class DebitCard {
    
    // Fee constants
    private static final double OVERDRAFT_FEE = 35.00; // Handled by checking team
    private static final double ATM_FEE = 5.00;
    private static final double CARD_REPLACEMENT_FEE = 7.50;
    private static final double MONTHLY_MAINTENANCE_FEE = 10.00;
    private static final double FOREIGN_TRANSACTION_FEE_RATE = 0.02; // 2%
    
    // Card properties
    private String debitCardNumber;
    private String pin;
    private String linkedCustomerId;
    private String linkedAccountId;
    private boolean isActive;
    private LocalDate issueDate;
    private LocalDate lastMaintenanceFeeDate;
    
    // CSV paths
    private static final Path CUSTOMER_CSV_PATH = Path.of("customerInfo.csv");
    
    // Reference to checking account system (provided by main banking system)
    private static List<BankingCSV.User> bankingUsers;
    
    public DebitCard(String debitCardNumber, String pin, String linkedCustomerId, String linkedAccountId) {
        this.debitCardNumber = debitCardNumber;
        this.pin = pin;
        this.linkedCustomerId = linkedCustomerId;
        this.linkedAccountId = linkedAccountId;
        this.isActive = true;
        this.issueDate = LocalDate.now();
        this.lastMaintenanceFeeDate = LocalDate.now();
    }
    
    // Main banking system calls this to provide access to checking accounts
    public static void setBankingUsers(List<BankingCSV.User> users) {
        bankingUsers = users;
    }
    
    // Generate random 16-digit Visa card number
    private static String generateCardNumber() {
        StringBuilder cardNumber = new StringBuilder("4532");
        Random rand = new Random();
        
        for (int i = 0; i < 12; i++) {
            cardNumber.append(rand.nextInt(10));
        }
        
        return cardNumber.toString();
    }
    
    // Update customerInfo.csv with hasDebitCard status
    private static void updateCustomerCSV(String customerId, boolean hasDebitCard) throws IOException {
        List<String> lines = Files.readAllLines(CUSTOMER_CSV_PATH);
        
        if (lines.isEmpty()) {
            System.out.println("Error: customerInfo.csv is empty");
            return;
        }
        
        // Find and update the customer's row
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] fields = line.split(",", -1);
            
            if (fields.length > 0 && fields[0].trim().equals(customerId)) {
                // Update hasDebitCard column (index 14)
                if (fields.length > 14) {
                    fields[14] = String.valueOf(hasDebitCard);
                }
                
                lines.set(i, String.join(",", fields));
                break;
            }
        }
        
        Files.write(CUSTOMER_CSV_PATH, lines);
    }
    
    // Issue a debit card to a customer
    public static DebitCard issueCard(String customerId, String pin) {
        try {
            // Check customerInfo.csv
            csvFile customerFile = new csvFile(CUSTOMER_CSV_PATH);
            Map<String, String> customerRecord = customerFile.getRecord("customerID", customerId);
            
            if (customerRecord == null) {
                System.out.println("Error: Customer not found in customerInfo.csv");
                return null;
            }
            
            String firstName = customerRecord.get("firstName");
            String lastName = customerRecord.get("lastName");
            String hasDebitCard = customerRecord.get("hasDebitCard");
            
            if (hasDebitCard != null && hasDebitCard.trim().equalsIgnoreCase("true")) {
                System.out.println("Customer already has a debit card.");
                return null;
            }
            
            // Find user in checking account system
            BankingCSV.User user = BankingCSV.findUser(bankingUsers, customerId);
            
            if (user == null) {
                System.out.println("Error: Customer not found in checking account system.");
                System.out.println("Please ensure customer has a checking account first.");
                return null;
            }
            
            if (user.accounts.isEmpty()) {
                System.out.println("Error: Customer has no checking accounts.");
                return null;
            }
            
            // Link to their first checking account
            String accountId = user.accounts.get(0).accountID;
            
            // Generate random 16-digit card number
            String cardNumber = generateCardNumber();
            
            // Create the card
            DebitCard card = new DebitCard(cardNumber, pin, customerId, accountId);
            
            // Update customerInfo.csv
            updateCustomerCSV(customerId, true);
            
            System.out.println("\n✓ Debit card issued successfully!");
            System.out.println("  Customer: " + firstName + " " + lastName);
            System.out.println("  Card Number: " + cardNumber);
            System.out.println("  Linked Account: " + accountId);
            System.out.println("  PIN: " + pin);
            
            return card;
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // Verify PIN for ATM access
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
    
    // Get card number
    public String getDebitCardNumber() {
        return debitCardNumber;
    }
    
    // Get linked customer ID
    public String getLinkedCustomerId() {
        return linkedCustomerId;
    }
    
    // Get linked account ID
    public String getLinkedAccountId() {
        return linkedAccountId;
    }
    
    // Check if card is active
    public boolean isActive() {
        return isActive;
    }
    
    // Check balance (integrates with BankingCSV)
    public double checkBalance() {
        if(!isActive) {
            System.out.println("Error: Card is not active");
            return 0.0;
        }
        
        BankingCSV.User user = BankingCSV.findUser(bankingUsers, linkedCustomerId);
        if (user == null) {
            System.out.println("Error: User not found in banking system");
            return 0.0;
        }
        
        // Find specific account balance
        for (BankingCSV.Account acc : user.accounts) {
            if (acc.accountID.equals(linkedAccountId)) {
                System.out.println("Balance for account " + linkedAccountId + ": $" + String.format("%.2f", acc.balance));
                return acc.balance;
            }
        }
        
        System.out.println("Error: Account not found");
        return 0.0;
    }
    
    // Regular withdrawal (integrates with BankingCSV - overdraft handled automatically)
    public boolean withdraw(double amount) {
        if(!isActive) {
            System.out.println("Error: Card is not active");
            return false;
        }
        
        if(amount <= 0) {
            System.out.println("Error: Amount must be greater than 0");
            return false;
        }
        
        BankingCSV.User user = BankingCSV.findUser(bankingUsers, linkedCustomerId);
        if (user == null) {
            System.out.println("Error: User not found in banking system");
            return false;
        }
        
        // Their withdraw method handles overdraft automatically
        user.withdraw(linkedAccountId, amount);
        
        return true;
    }
    
    // ATM withdrawal with $5 fee (integrates with BankingCSV)
    public boolean withdrawFromATM(double amount, boolean isOwnBankATM) {
        if(!isActive) {
            System.out.println("Error: Card is not active");
            return false;
        }
        
        if(amount <= 0) {
            System.out.println("Error: Amount must be greater than 0");
            return false;
        }
        
        double totalWithdrawal = amount;
        
        if (!isOwnBankATM) {
            System.out.println("ATM fee of $" + ATM_FEE + " applied (using non-network ATM).");
            totalWithdrawal += ATM_FEE;
        }
        
        System.out.println("Total withdrawal: $" + String.format("%.2f", totalWithdrawal));
        
        BankingCSV.User user = BankingCSV.findUser(bankingUsers, linkedCustomerId);
        if (user == null) {
            System.out.println("Error: User not found in banking system");
            return false;
        }
        
        // Withdraw total amount (their code handles overdraft)
        user.withdraw(linkedAccountId, totalWithdrawal);
        
        return true;
    }
    
    // Foreign transaction with 2% fee (integrates with BankingCSV)
    public boolean foreignTransaction(double amount, String currency) {
        if(!isActive) {
            System.out.println("Error: Card is not active");
            return false;
        }
        
        if(amount <= 0) {
            System.out.println("Error: Amount must be greater than 0");
            return false;
        }
        
        double foreignFee = amount * FOREIGN_TRANSACTION_FEE_RATE;
        double totalCharge = amount + foreignFee;
        
        System.out.println("Foreign transaction in " + currency);
        System.out.println("  Transaction amount: $" + String.format("%.2f", amount));
        System.out.println("  Foreign fee (" + (FOREIGN_TRANSACTION_FEE_RATE * 100) + "%): $" + String.format("%.2f", foreignFee));
        System.out.println("  Total charge: $" + String.format("%.2f", totalCharge));
        
        BankingCSV.User user = BankingCSV.findUser(bankingUsers, linkedCustomerId);
        if (user == null) {
            System.out.println("Error: User not found in banking system");
            return false;
        }
        
        // Withdraw total charge (their code handles overdraft)
        user.withdraw(linkedAccountId, totalCharge);
        
        return true;
    }
    
    // Deposit (integrates with BankingCSV)
    public boolean deposit(double amount) {
        if(!isActive) {
            System.out.println("Error: Card is not active");
            return false;
        }
        
        if(amount <= 0) {
            System.out.println("Error: Amount must be greater than 0");
            return false;
        }
        
        BankingCSV.User user = BankingCSV.findUser(bankingUsers, linkedCustomerId);
        if (user == null) {
            System.out.println("Error: User not found in banking system");
            return false;
        }
        
        // Call their deposit method
        user.deposit(linkedAccountId, amount);
        
        return true;
    }
    
    // Apply monthly maintenance fee (integrates with BankingCSV)
    public boolean applyMonthlyMaintenanceFee() {
        if(!isActive) {
            System.out.println("Error: Card is not active");
            return false;
        }
        
        LocalDate today = LocalDate.now();
        if (lastMaintenanceFeeDate.plusMonths(1).isAfter(today)) {
            System.out.println("Monthly maintenance fee already charged this month.");
            return false;
        }
        
        System.out.println("Applying monthly maintenance fee: $" + MONTHLY_MAINTENANCE_FEE);
        
        BankingCSV.User user = BankingCSV.findUser(bankingUsers, linkedCustomerId);
        if (user == null) {
            System.out.println("Error: User not found in banking system");
            return false;
        }
        
        // Withdraw maintenance fee (their code handles overdraft)
        user.withdraw(linkedAccountId, MONTHLY_MAINTENANCE_FEE);
        
        lastMaintenanceFeeDate = today;
        
        return true;
    }
    
    // Replace card with fee (integrates with BankingCSV)
    public DebitCard replaceCard(String reason) {
        if(!isActive) {
            System.out.println("Cannot replace an inactive card.");
            return null;
        }
        
        System.out.println("\nCard replacement requested.");
        System.out.println("  Reason: " + reason);
        System.out.println("  Replacement fee: $" + CARD_REPLACEMENT_FEE);
        
        BankingCSV.User user = BankingCSV.findUser(bankingUsers, linkedCustomerId);
        if (user == null) {
            System.out.println("Error: User not found in banking system");
            return null;
        }
        
        // Charge replacement fee (their code handles overdraft)
        user.withdraw(linkedAccountId, CARD_REPLACEMENT_FEE);
        
        // Close old card
        this.closeCard();
        
        // Generate new random card number
        String newCardNumber = generateCardNumber();
        
        // Create new card with same PIN and account
        DebitCard newCard = new DebitCard(newCardNumber, this.pin, this.linkedCustomerId, this.linkedAccountId);
        
        System.out.println("  New card issued: " + newCardNumber);
        
        return newCard;
    }
    
    // Close card (updates customerInfo.csv)
    public void closeCard() {
        this.isActive = false;
        System.out.println("Card " + debitCardNumber + " has been closed.");
        
        // Update customerInfo.csv
        try {
            updateCustomerCSV(linkedCustomerId, false);
            System.out.println("customerInfo.csv updated: hasDebitCard = false");
        } catch (IOException e) {
            System.out.println("Warning: Could not update customerInfo.csv");
        }
    }
    
    // Display fee schedule
    public void displayFeeSchedule() {
        System.out.println("\n=== Fee Schedule for Card " + debitCardNumber + " ===");
        System.out.println("Overdraft Fee: $" + OVERDRAFT_FEE + " (managed by checking account)");
        System.out.println("ATM Fee (non-network): $" + ATM_FEE);
        System.out.println("Card Replacement Fee: $" + CARD_REPLACEMENT_FEE);
        System.out.println("Monthly Maintenance Fee: $" + MONTHLY_MAINTENANCE_FEE);
        System.out.println("Foreign Transaction Fee: " + (FOREIGN_TRANSACTION_FEE_RATE * 100) + "%");
        System.out.println("Linked Account: " + linkedAccountId);
        System.out.println("Customer ID: " + linkedCustomerId);
        System.out.println("Card Status: " + (isActive ? "Active" : "Inactive"));
    }
}
