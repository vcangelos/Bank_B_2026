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

    // CSV Index Constants — hasDebitCard is column 14 in customerInfo.csv
    private static final int CSV_CUSTOMER_HAS_DEBIT_IDX = 14;

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
    private static List<CheckingAccount.CheckingUser> bankingUsers;

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

    public static void setBankingUsers(List<CheckingAccount.CheckingUser> users) {
        bankingUsers = users;
    }

    // --- GETTERS TO ACCESS DATES ---
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getLastMaintenanceFeeDate() { return lastMaintenanceFeeDate; }

    public static String generateCardNumber(String issuer) {
        StringBuilder cardNumber = new StringBuilder();
        Random rand = new Random();

        switch (issuer.toLowerCase()) {
            case "visa":
                cardNumber.append("4");
                for (int i = 0; i < 15; i++) cardNumber.append(rand.nextInt(10));
                break;
            case "mastercard":
                cardNumber.append("5");
                cardNumber.append(rand.nextInt(5) + 1);
                for (int i = 0; i < 14; i++) cardNumber.append(rand.nextInt(10));
                break;
            case "amex":
            case "american express":
                cardNumber.append("3");
                cardNumber.append(rand.nextBoolean() ? "4" : "7");
                for (int i = 0; i < 13; i++) cardNumber.append(rand.nextInt(10));
                break;
            case "discover":
                cardNumber.append("6011");
                for (int i = 0; i < 12; i++) cardNumber.append(rand.nextInt(10));
                break;
            default:
                cardNumber.append("4");
                for (int i = 0; i < 15; i++) cardNumber.append(rand.nextInt(10));
        }
        return cardNumber.toString();
    }

    public static String generateCardNumber() {
        return generateCardNumber("Visa");
    }

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
                // Safely update index 18
                if (fields.length > CSV_CUSTOMER_HAS_DEBIT_IDX) {
                    fields[CSV_CUSTOMER_HAS_DEBIT_IDX] = hasDebitCard ? "yes" : "no";
                }
                lines.set(i, String.join(",", fields));
                break;
            }
        }
        Files.write(CUSTOMER_CSV_PATH, lines);
    }

    // FIXED: Now saves issueDate and lastMaintenanceFeeDate into the CSV
    private static void saveToDebitCardCSV(String userid, String firstName, String lastName, String pin, LocalDate issue, LocalDate lastFee) throws IOException {
        if (!Files.exists(DEBIT_CARD_CSV_PATH)) {
            // Added the two new columns to the header
            List<String> headers = java.util.Arrays.asList("userid,firstname,lastname,debitBalance,debitPin,issueDate,lastMaintenanceFeeDate");
            Files.write(DEBIT_CARD_CSV_PATH, headers);
        }

        String line = String.format("%s,%s,%s,%.2f,%s,%s,%s",
                userid, firstName, lastName, 0.00, pin, issue.toString(), lastFee.toString()
        );

        Files.write(DEBIT_CARD_CSV_PATH, java.util.Arrays.asList(line), StandardOpenOption.APPEND);
    }

    private static void updateDebitBalance(String userid, double newBalance) throws IOException {
        if (!Files.exists(DEBIT_CARD_CSV_PATH)) return;
        List<String> lines = Files.readAllLines(DEBIT_CARD_CSV_PATH);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] fields = line.split(",", -1);

            if (fields.length > 0 && fields[0].trim().equals(userid)) {
                fields[3] = String.format("%.2f", newBalance);
                lines.set(i, String.join(",", fields));
                break;
            }
        }
        Files.write(DEBIT_CARD_CSV_PATH, lines);
    }

    // FIXED: New method to permanently save the maintenance date to CSV so it survives restarts
    private static void updateMaintenanceDateInCSV(String userid, LocalDate newDate) throws IOException {
        if (!Files.exists(DEBIT_CARD_CSV_PATH)) return;
        List<String> lines = Files.readAllLines(DEBIT_CARD_CSV_PATH);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] fields = line.split(",", -1);

            if (fields.length > 0 && fields[0].trim().equals(userid)) {
                if (fields.length > 6) { // 6 is the lastMaintenanceFeeDate column
                    fields[6] = newDate.toString();
                    lines.set(i, String.join(",", fields));
                    break;
                }
            }
        }
        Files.write(DEBIT_CARD_CSV_PATH, lines);
    }

    public static String getPINFromCSV(String userid) {
        try {
            if (!Files.exists(DEBIT_CARD_CSV_PATH)) return null;

            List<String> lines = Files.readAllLines(DEBIT_CARD_CSV_PATH);
            if (lines.isEmpty()) return null;

            String[] headers = lines.get(0).split(",");

            // Find the index of the "debitPin" column
            int pinIndex = -1;
            int userIdIndex = -1;

            for (int i = 0; i < headers.length; i++) {
                String header = headers[i].trim().toLowerCase();
                if (header.equals("debitpin")) pinIndex = i;
                if (header.equals("userid")) userIdIndex = i;
            }

            if (pinIndex == -1 || userIdIndex == -1) {
                System.out.println("Error: Required columns not found in debitCard.csv");
                return null;
            }

            // Search for the matching user
            for (int i = 1; i < lines.size(); i++) {
                String[] values = lines.get(i).split(",", -1);

                if (values.length > Math.max(pinIndex, userIdIndex) &&
                        values[userIdIndex].trim().equals(userid)) {

                    return values[pinIndex].trim();
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading PIN from CSV: " + e.getMessage());
        }

        return null;
    }

    public static DebitCard issueCard(Scanner scanner, String customerId) {
        try {
            Map<String, String> customerRecord = new java.util.HashMap<>();

            List<String> lines = Files.readAllLines(CUSTOMER_CSV_PATH);
            if (lines.isEmpty()) {
                System.out.println("Error: customerInfo.csv is empty");
                return null;
            }

            String[] headers = lines.get(0).split(",");

            for (int i = 1; i < lines.size(); i++) {
                String[] values = lines.get(i).split(",", -1);

                if (values[0].trim().equals(customerId)) { // assuming customerID is column 0
                    for (int j = 0; j < headers.length; j++) {
                        customerRecord.put(headers[j].trim(), values[j].trim());
                    }
                    break;
                }
            }

            if (customerRecord.isEmpty()) {
                System.out.println("Error: Customer not found in customerInfo.csv");
                return null;
            }

            if (customerRecord == null) {
                System.out.println("Error: Customer not found in customerInfo.csv");
                return null;
            }

            String firstName = customerRecord.get("firstName");
            String lastName = customerRecord.get("lastName");

            String hasDebitCardValue = customerRecord.get("hasDebitCard");
            boolean hasDebitCard = hasDebitCardValue != null && "yes".equalsIgnoreCase(hasDebitCardValue);

            if (hasDebitCard) {
                System.out.println("Customer already has a debit card.");
                return null;
            }

            if (bankingUsers == null) {
                System.out.println("Error: Banking system not initialized. Please set banking users first.");
                return null;
            }

            CheckingAccount.CheckingUser user = CheckingAccount.findUser(bankingUsers, customerId);
            if (user == null) {
                System.out.println("Error: Customer not found in checking account system.");
                return null;
            }
            if (user.accounts.isEmpty()) {
                System.out.println("Error: Customer has no checking accounts.");
                return null;
            }

            String accountId = user.accounts.get(0).accountID;

            System.out.println("\n=== Select Card Issuer ===");
            System.out.println("1. Visa\n2. Mastercard\n3. American Express (Amex)\n4. Discover");
            System.out.print("\nSelect card issuer (1-4): ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String cardIssuer;
            switch (choice) {
                case 1: cardIssuer = "Visa"; break;
                case 2: cardIssuer = "Mastercard"; break;
                case 3: cardIssuer = "American Express"; break;
                case 4: cardIssuer = "Discover"; break;
                default: cardIssuer = "Visa";
            }

            String pin = "";
            boolean validPin = false;
            while (!validPin) {
                System.out.print("\nCreate a 4-digit PIN: ");
                pin = scanner.nextLine().trim();
                if (pin.length() != 4 || !pin.matches("\\d{4}")) {
                    System.out.println("Error: PIN must be exactly 4 digits (0-9).");
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

            String cardNumber = DebitCard.generateCardNumber(cardIssuer);
            DebitCard card = new DebitCard(cardNumber, cardIssuer, pin, customerId, accountId);

            updateCustomerCSV(customerId, true);

            // FIXED: Passing the dates into the save method so they are permanently stored
            saveToDebitCardCSV(customerId, firstName, lastName, pin, card.getIssueDate(), card.getLastMaintenanceFeeDate());

            System.out.println("\n✓ Debit card issued successfully! (Card Number: " + cardNumber + ")");
            return card;

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public boolean verifyPin(String enteredPin) {
        if(!isActive || enteredPin == null) return false;
        return this.pin.equals(enteredPin);
    }

    public String getDebitCardNumber() { return debitCardNumber; }
    public String getCardIssuer() { return cardIssuer; }
    public String getLinkedCustomerId() { return linkedCustomerId; }
    public String getLinkedAccountId() { return linkedAccountId; }
    public boolean isActive() { return isActive; }

    public double checkBalance() {
        if(!isActive || bankingUsers == null) return 0.0;

        CheckingAccount.CheckingUser user = CheckingAccount.findUser(bankingUsers, linkedCustomerId);
        if (user == null) return 0.0;

        for (CheckingAccount.Account acc : user.accounts) {
            if (acc.accountID.equals(linkedAccountId)) {
                System.out.println("Balance: $" + String.format("%.2f", acc.balance));
                try { updateDebitBalance(linkedCustomerId, acc.balance); } catch (IOException e) {}
                return acc.balance;
            }
        }
        return 0.0;
    }

    public boolean withdraw(double amount) {
        if(!isActive || amount <= 0 || bankingUsers == null) return false;

        CheckingAccount.CheckingUser user = CheckingAccount.findUser(bankingUsers, linkedCustomerId);
        if (user == null) return false;

        for (CheckingAccount.Account acc : user.accounts) {
            if (acc.accountID.equals(linkedAccountId)) {
                double beforeBalance = acc.balance;
                user.withdraw(linkedAccountId, amount);

                // If balance didn't change, the checking account denied the withdrawal
                if (acc.balance == beforeBalance) return false;

                try { updateDebitBalance(linkedCustomerId, acc.balance); } catch (IOException e) {}
                return true;
            }
        }
        return false;
    }

    public boolean withdrawFromATM(double amount, boolean isOwnBankATM) {
        if(!isActive || amount <= 0 || bankingUsers == null) return false;

        double totalWithdrawal = amount + (isOwnBankATM ? 0 : ATM_FEE);
        if (!isOwnBankATM) System.out.println("ATM fee applied.");

        CheckingAccount.CheckingUser user = CheckingAccount.findUser(bankingUsers, linkedCustomerId);
        if (user == null) return false;

        for (CheckingAccount.Account acc : user.accounts) {
            if (acc.accountID.equals(linkedAccountId)) {
                double beforeBalance = acc.balance;
                user.withdraw(linkedAccountId, totalWithdrawal);

                if (acc.balance == beforeBalance) return false;
                try { updateDebitBalance(linkedCustomerId, acc.balance); } catch (IOException e) { }
                return true;
            }
        }
        return false;
    }

    public boolean foreignTransaction(double amount, String currency) {
        if(!isActive || amount <= 0 || bankingUsers == null) return false;

        double totalCharge = amount + (amount * FOREIGN_TRANSACTION_FEE_RATE);

        CheckingAccount.CheckingUser user = CheckingAccount.findUser(bankingUsers, linkedCustomerId);
        if (user == null) return false;

        for (CheckingAccount.Account acc : user.accounts) {
            if (acc.accountID.equals(linkedAccountId)) {
                double beforeBalance = acc.balance;
                user.withdraw(linkedAccountId, totalCharge);

                if (acc.balance == beforeBalance) return false;
                try { updateDebitBalance(linkedCustomerId, acc.balance); } catch (IOException e) {}
                return true;
            }
        }
        return false;
    }

    public boolean deposit(double amount) {
        if(!isActive || amount <= 0 || bankingUsers == null) return false;

        CheckingAccount.CheckingUser user = CheckingAccount.findUser(bankingUsers, linkedCustomerId);
        if (user == null) return false;

        user.deposit(linkedAccountId, amount);
        for (CheckingAccount.Account acc : user.accounts) {
            if (acc.accountID.equals(linkedAccountId)) {
                try { updateDebitBalance(linkedCustomerId, acc.balance); } catch (IOException e) {}
                break;
            }
        }
        return true;
    }

    public boolean applyMonthlyMaintenanceFee() {
        if(!isActive || bankingUsers == null) return false;

        LocalDate today = LocalDate.now();
        if (lastMaintenanceFeeDate.plusMonths(1).isAfter(today)) {
            System.out.println("Monthly maintenance fee already charged this month.");
            return false;
        }

        CheckingAccount.CheckingUser user = CheckingAccount.findUser(bankingUsers, linkedCustomerId);
        if (user == null) return false;

        for (CheckingAccount.Account acc : user.accounts) {
            if (acc.accountID.equals(linkedAccountId)) {
                double beforeBalance = acc.balance;
                user.withdraw(linkedAccountId, MONTHLY_MAINTENANCE_FEE);

                // FIXED: Actually records the date change and saves it to the file permanently!
                if (acc.balance != beforeBalance) {
                    lastMaintenanceFeeDate = today;
                    try {
                        updateDebitBalance(linkedCustomerId, acc.balance);
                        updateMaintenanceDateInCSV(linkedCustomerId, today);
                    } catch (IOException e) {
                        System.out.println("Warning: Could not update CSV dates.");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public DebitCard replaceCard(Scanner scanner, String reason) {
        if(!isActive || bankingUsers == null) return null;

        CheckingAccount.CheckingUser user = CheckingAccount.findUser(bankingUsers, linkedCustomerId);
        if (user == null) return null;

        double beforeBalance = user.accounts.get(0).balance;
        user.withdraw(linkedAccountId, CARD_REPLACEMENT_FEE);
        if (user.accounts.get(0).balance == beforeBalance) {
            System.out.println("Insufficient funds for replacement fee.");
            return null;
        }

        System.out.println("\nCurrent card issuer: " + this.cardIssuer);
        System.out.print("Keep the same issuer? (yes/no): ");
        String keepSame = scanner.next();

        String newIssuer = this.cardIssuer;
        if (!keepSame.equalsIgnoreCase("yes") && !keepSame.equalsIgnoreCase("y")) {
            System.out.println("1. Visa\n2. Mastercard\n3. Amex\n4. Discover");
            System.out.print("Select (1-4): ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1: newIssuer = "Visa"; break;
                case 2: newIssuer = "Mastercard"; break;
                case 3: newIssuer = "American Express"; break;
                case 4: newIssuer = "Discover"; break;
            }
        }

        String newCardNumber = DebitCard.generateCardNumber(newIssuer);

        // FIXED LOGIC: Make the new card BEFORE destroying the old one
        DebitCard newCard = new DebitCard(newCardNumber, newIssuer, this.pin, this.linkedCustomerId, this.linkedAccountId);
        this.closeCard();

        System.out.println("\n✓ New card issued: " + newCardNumber);
        return newCard;
    }

    public void closeCard() {
        this.isActive = false;
        System.out.println("Card " + debitCardNumber + " has been closed.");
        try {
            updateCustomerCSV(linkedCustomerId, false);
        } catch (IOException e) {
            System.out.println("Warning: Could not update customerInfo.csv");
        }
    }

    public void displayFeeSchedule() {
        System.out.println("\n=== Fee Schedule for Card " + debitCardNumber + " ===");
        System.out.println("Card Issuer: " + cardIssuer);
        System.out.println("Issue Date: " + issueDate);
        System.out.println("Last Maintenance Fee Date: " + lastMaintenanceFeeDate);
        System.out.println("Overdraft Fee: $" + OVERDRAFT_FEE + " (managed by checking)");
        System.out.println("ATM Fee (non-network): $" + ATM_FEE);
        System.out.println("Card Replacement Fee: $" + CARD_REPLACEMENT_FEE);
        System.out.println("Monthly Maintenance Fee: $" + MONTHLY_MAINTENANCE_FEE);
        System.out.println("Foreign Transaction Fee: " + (FOREIGN_TRANSACTION_FEE_RATE * 100) + "%");
        System.out.println("Card Status: " + (isActive ? "Active" : "Inactive"));
    }

    public static DebitCard loadCard(String customerId) {
        try {
            if (!Files.exists(DEBIT_CARD_CSV_PATH)) return null;

            List<String> lines = Files.readAllLines(DEBIT_CARD_CSV_PATH);
            if (lines.isEmpty()) return null;

            String[] headers = lines.get(0).split(",");

            int userIdIndex = -1, pinIndex = -1, issueIndex = -1, feeIndex = -1;

            for (int i = 0; i < headers.length; i++) {
                String h = headers[i].trim().toLowerCase();
                if (h.equals("userid")) userIdIndex = i;
                if (h.equals("debitpin")) pinIndex = i;
                if (h.equals("issuedate")) issueIndex = i;
                if (h.equals("lastmaintenancefeedate")) feeIndex = i;
            }

            for (int i = 1; i < lines.size(); i++) {
                String[] values = lines.get(i).split(",", -1);

                if (values[userIdIndex].trim().equals(customerId)) {

                    String pin = values[pinIndex].trim();
                    String cardNumber = generateCardNumber(); // not stored, so regenerate
                    String accountId = CheckingAccount.findUser(bankingUsers, customerId).accounts.get(0).accountID;

                    DebitCard card = new DebitCard(cardNumber, pin, customerId, accountId);

                    // restore dates
                    if (issueIndex != -1)
                        card.issueDate = LocalDate.parse(values[issueIndex].trim());
                    if (feeIndex != -1)
                        card.lastMaintenanceFeeDate = LocalDate.parse(values[feeIndex].trim());

                    return card;
                }
            }

        } catch (Exception e) {
            System.out.println("Error loading debit card: " + e.getMessage());
        }

        return null;
    }
}