import java.time.LocalDate;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.io.IOException;

public class DebitCard {
    
    // Fee constants
    private static final double OVERDRAFT_FEE = 35.00;
    private static final double ATM_FEE = 5.00;
    private static final double CARD_REPLACEMENT_FEE = 7.50;
    private static final double MONTHLY_MAINTENANCE_FEE = 10.00;
    private static final double FOREIGN_TRANSACTION_FEE_RATE = 0.02;
    
    // Card properties
    private String debitCardNumber;
    private String cardIssuer;
    private String pin;
    private String linkedCustomerId;
    private String linkedAccountId;
    private boolean isActive;
    private LocalDate issueDate;
    private LocalDate lastMaintenanceFeeDate;
    
    // CSV paths
    private static final Path CUSTOMER_CSV_PATH = Path.of("customerInfo.csv");
    private static final Path DEBIT_CARD_CSV_PATH = Path.of("debitCard.csv");
    
    // Reference to checking account system
    private static List<BankingCSV.User> bankingUsers;
    
    public DebitCard(String debitCardNumber, String cardIssuer, String pin, String linkedCustomerId, String linkedAccountId) {
        this.debitCardNumber = debitCardNumber;
        this.cardIssuer = cardIssuer;
        this.pin = pin;
        this.linkedCustomerId = linkedCustomerId;
        this.linkedAccountId = linkedAccountId;
        this.isActive = true;
        this.issueDate = LocalDate.now();
        this.lastMaintenanceFeeDate = LocalDate.now();
    }
    
    public DebitCard(String debitCardNumber, String pin, String linkedCustomerId, String linkedAccountId) {
        this(debitCardNumber, "Visa", pin, linkedCustomerId, linkedAccountId);
    }
    
    public static void setBankingUsers(List<BankingCSV.User> users) {
        bankingUsers = users;
    }
    
    // OVERLOADED: Generate card number with issuer
    public static String generateCardNumber(String issuer) {
        StringBuilder cardNumber = new StringBuilder();
        Random rand = new Random();
        
        switch (issuer.toLowerCase()) {
            case "visa":
                cardNumber.append("4");
                for (int i = 0; i < 15; i++) {
                    cardNumber.append(rand.nextInt(10));
                }
                break;
                
            case "mastercard":
                cardNumber.append("5");
                cardNumber.append(rand.nextInt(5) + 1);
                for (int i = 0; i < 14; i++) {
                    cardNumber.append(rand.nextInt(10));
                }
                break;
                
            case "amex":
            case "american express":
                cardNumber.append("3");
                cardNumber.append(rand.nextBoolean() ? "4" : "7");
                for (int i = 0; i < 13; i++) {
                    cardNumber.append(rand.nextInt(10));
                }
                break;
                
            case "discover":
                cardNumber.append("6011");
                for (int i = 0; i < 12; i++) {
                    cardNumber.append(rand.nextInt(10));
                }
                break;
                
            default:
                cardNumber.append("4");
                for (int i = 0; i < 15; i++) {
                    cardNumber.append(rand.nextInt(10));
                }
        }
        
        return cardNumber.toString();
    }
    
    // OVERLOADED: Generate card number without issuer (defaults to Visa)
    public static String generateCardNumber() {
        return generateCardNumber("Visa");
    }
    
    // Update customerInfo.csv
    private static void updateCustomerCSV(String customerId, boolean hasDebitCard) throws IOException {
        List<String> lines = Files.readAllLines(CUSTOMER_CSV_PATH);
        
        if (lines.isEmpty()) {
            System.out.println("Error: customerInfo.csv is empty");
            return;
        }
        
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] fields = line.split(",", -1);
            
            if (fields.length > 0 && fields[0].trim().equals(customerId)) {
                // FIXED: Checking index 18 and writing "yes" or "no"
                if (fields.length > 18) {
                    fields[18] = hasDebitCard ? "yes" : "no";
                }
                
                lines.set(i, String.join(",", fields));
                break;
            }
        }
        
        Files.write(CUSTOMER_CSV_PATH, lines);
    }
    
    // Save to debitCard.csv
    private static void saveToDebitCardCSV(String userid, String firstName, String lastName, String pin) throws IOException {
        if (!Files.exists(DEBIT_CARD_CSV_PATH)) {
            List<String> headers = java.util.Arrays.asList("userid, firstname, lastname, debitBalance, debitPin");
            Files.write(DEBIT_CARD_CSV_PATH, headers);
        }
        
        String line = String.format("%s, %s, %s, %.2f, %s",
            userid,
            firstName,
            lastName,
            0.00,
            pin
        );
        
        Files.write(DEBIT_CARD_CSV_PATH, java.util.Arrays.asList(line),
            StandardOpenOption.APPEND);
    }
    
    // Update debitBalance in debitCard.csv
    private static void updateDebitBalance(String userid, double newBalance) throws IOException {
        if (!Files.exists(DEBIT_CARD_CSV_PATH)) {
            return;
        }
        
        List<String> lines = Files.readAllLines(DEBIT_CARD_CSV_PATH);
        
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] fields = line.split(",");
            
            if (fields.length > 0 && fields[0].trim().equals(userid)) {
                fields[3] = String.format(" %.2f", newBalance);
                lines.set(i, String.join(",", fields));
                break;
            }
        }
        
        Files.write(DEBIT_CARD_CSV_PATH, lines);
    }
    
    // Get PIN from debitCard.csv
    public static String getPINFromCSV(String userid) {
        try {
            if (!Files.exists(DEBIT_CARD_CSV_PATH)) {
                return null;
            }
            
            csvFile debitFile = new csvFile(DEBIT_CARD_CSV_PATH);
            Map<String, String> record = debitFile.getRecord("userid", userid);
            
            if (record != null) {
                return record.get("debitPin").trim();
            }
        } catch (IOException e) {
            System.out.println("Error reading PIN from CSV: " + e.getMessage());
        }
        
        return null;
    }
    
    // Issue card for regular customer with PIN creation
    public static DebitCard issueCard(Scanner scanner, String customerId) {
        try {
            csvFile customerFile = new csvFile(CUSTOMER_CSV_PATH);
            Map<String, String> customerRecord = customerFile.getRecord("customerID", customerId);
            
            if (customerRecord == null) {
                System.out.println("Error: Customer not found in customerInfo.csv");
                return null;
            }
            
            String firstName = customerRecord.get("firstName");
            String lastName = customerRecord.get("lastName");
            
            // FIXED: Clean one-line boolean expression checking for "yes"
            boolean hasDebitCard = "yes".equalsIgnoreCase(customerRecord.get("hasDebitCard"));
            
            if (hasDebitCard) {
                System.out.println("Customer already has a debit card.");
                return null;
            }
            
            BankingCSV.User user = BankingCSV.findUser(bankingUsers, customerId);
            
            if (user == null) {
                System.out.println("Error: Customer not found in checking account system.");
                return null;
            }
            
            if (user.accounts.isEmpty()) {
                System.out.println("Error: Customer has no checking accounts.");
                return null;
            }
            
            String accountId = user.accounts.get(0).accountID;
            
            // STEP 1: Select card issuer
            System.out.println("\n=== Select Card Issuer ===");
            System.out.println("1. Visa");
            System.out.println("2. Mastercard");
            System.out.println("3. American Express (Amex)");
            System.out.println("4. Discover");
            
            System.out.print("\nSelect card issuer (1-4): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); 
            
            String cardIssuer;
            switch (choice) {
                case 1: cardIssuer = "Visa"; break;
                case 2: cardIssuer = "Mastercard"; break;
                case 3: cardIssuer = "American Express"; break;
                case 4: cardIssuer = "Discover"; break;
                default:
                    System.out.println("Invalid selection. Defaulting to Visa.");
                    cardIssuer = "Visa";
            }
            
            // STEP 2: Create PIN
            String pin = "";
            boolean validPin = false;
            
            while (!validPin) {
                System.out.print("\nCreate a 4-digit PIN: ");
                pin = scanner.nextLine().trim();
                
                if (pin.length() != 4) {
                    System.out.println("Error: PIN must be exactly 4 digits.");
                    continue;
                }
                
                boolean allDigits = true;
                for (char c : pin.toCharArray()) {
                    if (!Character.isDigit(c)) {
                        allDigits = false;
                        break;
                    }
                }
                
                if (!allDigits) {
                    System.out.println("Error: PIN must contain only numbers (0-9).");
                    continue;
                }
                
                System.out.print("Confirm your 4-digit PIN: ");
                String confirmPin = scanner.nextLine().trim();
                
                if (!pin.equals(confirmPin)) {
                    System.out.println("Error: PINs do not match. Please try again.");
                    continue;
                }
                
                validPin = true;
            }
            
            // STEP 3: Generate card number
            String cardNumber = DebitCard.generateCardNumber(cardIssuer);
            
            // STEP 4: Create the card
            DebitCard card = new DebitCard(cardNumber, cardIssuer, pin, customerId, accountId);
            
            // STEP 5: Update customerInfo.csv
            updateCustomerCSV(customerId, true);
            
            // STEP 6: Save to debitCard.csv
            saveToDebitCardCSV(customerId, firstName, lastName, pin);
            
            System.out.println("\n✓ Debit card issued successfully!");
            System.out.println("  Customer: " + firstName + " " + lastName);
            System.out.println("  Card Issuer: " + cardIssuer);
            System.out.println("  Card Number: " + cardNumber);
            System.out.println("  Card Length: " + cardNumber.length() + " digits");
            System.out.println("  Linked Account: " + accountId);
            System.out.println("  PIN: ****");
            System.out.println("\n⚠ IMPORTANT: Remember your PIN! You will need it to use your card.");
            
            return card;
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
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
    
    public String getDebitCardNumber() {
        return debitCardNumber;
    }
    
    public String getCardIssuer() {
        return cardIssuer;
    }
    
    public String getLinkedCustomerId() {
        return linkedCustomerId;
    }
    
    public String getLinkedAccountId() {
        return linkedAccountId;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
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
        
        for (BankingCSV.Account acc : user.accounts) {
            if (acc.accountID.equals(linkedAccountId)) {
                System.out.println("Balance for account " + linkedAccountId + ": $" + String.format("%.2f", acc.balance));
                
                try {
                    updateDebitBalance(linkedCustomerId, acc.balance);
                } catch (IOException e) {
                    System.out.println("Warning: Could not update debitCard.csv");
                }
                
                return acc.balance;
            }
        }
        
        System.out.println("Error: Account not found");
        return 0.0;
    }
    
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
        
        user.withdraw(linkedAccountId, amount);
        
        for (BankingCSV.Account acc : user.accounts) {
            if (acc.accountID.equals(linkedAccountId)) {
                try {
                    updateDebitBalance(linkedCustomerId, acc.balance);
                } catch (IOException e) {
                    System.out.println("Warning: Could not update debitCard.csv");
                }
                break;
            }
        }
        
        return true;
    }
    
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
        
        user.withdraw(linkedAccountId, totalWithdrawal);
        
        for (BankingCSV.Account acc : user.accounts) {
            if (acc.accountID.equals(linkedAccountId)) {
                try {
                    updateDebitBalance(linkedCustomerId, acc.balance);
                } catch (IOException e) {
                    System.out.println("Warning: Could not update debitCard.csv");
                }
                break;
            }
        }
        
        return true;
    }
    
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
        
        user.withdraw(linkedAccountId, totalCharge);
        
        for (BankingCSV.Account acc : user.accounts) {
            if (acc.accountID.equals(linkedAccountId)) {
                try {
                    updateDebitBalance(linkedCustomerId, acc.balance);
                } catch (IOException e) {
                    System.out.println("Warning: Could not update debitCard.csv");
                }
                break;
            }
        }
        
        return true;
    }
    
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
        
        user.deposit(linkedAccountId, amount);
        
        for (BankingCSV.Account acc : user.accounts) {
            if (acc.accountID.equals(linkedAccountId)) {
                try {
                    updateDebitBalance(linkedCustomerId, acc.balance);
                } catch (IOException e) {
                    System.out.println("Warning: Could not update debitCard.csv");
                }
                break;
            }
        }
        
        return true;
    }
    
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
        
        user.withdraw(linkedAccountId, MONTHLY_MAINTENANCE_FEE);
        
        lastMaintenanceFeeDate = today;
        
        for (BankingCSV.Account acc : user.accounts) {
            if (acc.accountID.equals(linkedAccountId)) {
                try {
                    updateDebitBalance(linkedCustomerId, acc.balance);
                } catch (IOException e) {
                    System.out.println("Warning: Could not update debitCard.csv");
                }
                break;
            }
        }
        
        return true;
    }
    
    public DebitCard replaceCard(Scanner scanner, String reason) {
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
        
        user.withdraw(linkedAccountId, CARD_REPLACEMENT_FEE);
        
        this.closeCard();
        
        System.out.println("\nCurrent card issuer: " + this.cardIssuer);
        System.out.print("Keep the same issuer? (yes/no): ");
        String keepSame = scanner.next();
        
        String newIssuer;
        if (keepSame.equalsIgnoreCase("yes") || keepSame.equalsIgnoreCase("y")) {
            newIssuer = this.cardIssuer;
        } else {
            System.out.println("\n=== Select New Card Issuer ===");
            System.out.println("1. Visa");
            System.out.println("2. Mastercard");
            System.out.println("3. American Express (Amex)");
            System.out.println("4. Discover");
            
            System.out.print("\nSelect card issuer (1-4): ");
            int choice = scanner.nextInt();
            
            switch (choice) {
                case 1: newIssuer = "Visa"; break;
                case 2: newIssuer = "Mastercard"; break;
                case 3: newIssuer = "American Express"; break;
                case 4: newIssuer = "Discover"; break;
                default:
                    System.out.println("Invalid selection. Keeping " + this.cardIssuer);
                    newIssuer = this.cardIssuer;
            }
        }
        
        String newCardNumber = DebitCard.generateCardNumber(newIssuer);
        
        DebitCard newCard = new DebitCard(newCardNumber, newIssuer, this.pin, this.linkedCustomerId, this.linkedAccountId);
        
        System.out.println("\n✓ New card issued:");
        System.out.println("  Issuer: " + newIssuer);
        System.out.println("  Card Number: " + newCardNumber);
        
        return newCard;
    }
    
    public void closeCard() {
        this.isActive = false;
        System.out.println("Card " + debitCardNumber + " has been closed.");
        
        try {
            updateCustomerCSV(linkedCustomerId, false);
            System.out.println("customerInfo.csv updated: hasDebitCard = no");
        } catch (IOException e) {
            System.out.println("Warning: Could not update customerInfo.csv");
        }
    }
    
    public void displayFeeSchedule() {
        System.out.println("\n=== Fee Schedule for Card " + debitCardNumber + " ===");
        System.out.println("Card Issuer: " + cardIssuer);
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
