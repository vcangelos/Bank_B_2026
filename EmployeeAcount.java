// EmployeeAccount.java

import java.util.*;
import java.nio.file.*;
import java.io.*;

/**
 * Represents an employee who has a bank account with this bank.
 * Links together: BankingCSV (checking), DebitCard, CreditCard, SavingsAccount.
 *
 * FIXES:
 * - Removed all csvFile references — replaced with plain Java file I/O
 * - Reads/writes employeecards.csv to persist card numbers across runs
 * - Loads or creates a SavingsAccount and records it in EmployeeSavings.csv
 * - Updates customerInfo.csv EmployeeAccount column (index 14) when created
 */
public class EmployeeAccount {

    private String employeeID;
    private String name;

    private BankingCSV.User bankingUser;
    private DebitCard debitCard;
    private CreditCard creditCard;
    private SavingsAccount savingsAccount;

    private static final Path CARDS_CSV          = Path.of("employeecards.csv");
    private static final Path EMPLOYEE_SAV_CSV   = Path.of("EmployeeSavings.csv");
    private static final Path CUSTOMER_INFO_CSV  = Path.of("customerInfo.csv");

    // -------------------------------------------------
    // Constructor
    // -------------------------------------------------

    public EmployeeAccount(String employeeID, String name, List<BankingCSV.User> existingUsers) {

        this.employeeID = employeeID;
        this.name = name;

        // STEP 1: Find or create BankingCSV user (checking account)
        this.bankingUser = BankingCSV.findUser(existingUsers, employeeID);
        if (this.bankingUser == null) {
            this.bankingUser = new BankingCSV.User(employeeID, name);
            existingUsers.add(this.bankingUser);
        }
        DebitCard.setBankingUsers(existingUsers);

        // STEP 2: Load or generate DebitCard
        String existingDebitNumber = loadDebitCardNumber(employeeID);
        String linkedAccountID = this.bankingUser.accounts.isEmpty()
                ? "NEW_ACCOUNT"
                : this.bankingUser.accounts.get(0).accountID;

        if (existingDebitNumber != null) {
            // Reuse saved card number
            this.debitCard = new DebitCard(existingDebitNumber, "1234", employeeID, linkedAccountID);
        } else {
            // Generate new card and save it
            String newDebitNumber = DebitCard.generateCardNumber();
            this.debitCard = new DebitCard(newDebitNumber, "1234", employeeID, linkedAccountID);
            saveCardToCSV(employeeID, newDebitNumber);
        }

        // STEP 3: Create CreditCard
        this.creditCard = new CreditCard();

        // STEP 4: Load or create SavingsAccount
        this.savingsAccount = loadOrCreateSavings(employeeID);

        // STEP 5: Mark EmployeeAccount column in customerInfo.csv
        updateCustomerInfoCSV(employeeID, true);
    }

    // -------------------------------------------------
    // Load existing debit card number from employeecards.csv
    // -------------------------------------------------

    private String loadDebitCardNumber(String employeeID) {
        if (!Files.exists(CARDS_CSV)) return null;

        try (BufferedReader br = Files.newBufferedReader(CARDS_CSV)) {
            String headerLine = br.readLine();
            if (headerLine == null) return null;

            String[] headers = headerLine.split(",", -1);
            int idIdx = -1, debitIdx = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equals("employeeID"))       idIdx    = i;
                if (headers[i].trim().equals("debitCardNumber"))  debitIdx = i;
            }
            if (idIdx == -1 || debitIdx == -1) return null;

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(",", -1);
                if (fields.length > idIdx && fields[idIdx].trim().equals(employeeID)) {
                    return fields.length > debitIdx ? fields[debitIdx].trim() : null;
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not read employeecards.csv: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------
    // Save new debit card number to employeecards.csv
    // -------------------------------------------------

    private void saveCardToCSV(String employeeID, String debitNumber) {
        try {
            boolean fileExists = Files.exists(CARDS_CSV);

            if (!fileExists) {
                // Create file with header first
                try (BufferedWriter bw = Files.newBufferedWriter(CARDS_CSV, StandardOpenOption.CREATE)) {
                    bw.write("employeeID,creditCardNumber,debitCardNumber");
                    bw.newLine();
                }
            }

            // Append the new row
            try (BufferedWriter bw = Files.newBufferedWriter(CARDS_CSV, StandardOpenOption.APPEND)) {
                bw.write(employeeID + ",," + debitNumber);
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Warning: Could not write to employeecards.csv: " + e.getMessage());
        }
    }

    // -------------------------------------------------
    // Load or create SavingsAccount, record in EmployeeSavings.csv
    // -------------------------------------------------

    private SavingsAccount loadOrCreateSavings(String employeeID) {
        try {
            if (SavingsAccount.userIDExists(employeeID)) {
                // Employee already has savings — open it
                return SavingsAccount.OpenSavingsAccount(employeeID);
            } else {
                // Create a new savings account with default $100 balance
                SavingsAccount newAcc = SavingsAccount.createSavingsAccount(employeeID, 100);
                recordEmployeeSavings(employeeID, newAcc.getSavingsID(), newAcc.getSavings());
                return newAcc;
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not load/create savings account: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Note: " + e.getMessage());
        }
        return null;
    }

    // -------------------------------------------------
    // Write to EmployeeSavings.csv
    // -------------------------------------------------

    private void recordEmployeeSavings(String employeeID, String savingsID, double balance) {
        try {
            boolean fileExists = Files.exists(EMPLOYEE_SAV_CSV);

            if (!fileExists) {
                try (BufferedWriter bw = Files.newBufferedWriter(EMPLOYEE_SAV_CSV, StandardOpenOption.CREATE)) {
                    bw.write("userid,SavingsID,Savings");
                    bw.newLine();
                }
            }

            // Check if already recorded before appending
            boolean alreadyThere = false;
            try (BufferedReader br = Files.newBufferedReader(EMPLOYEE_SAV_CSV)) {
                br.readLine(); // skip header
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(employeeID + ",")) {
                        alreadyThere = true;
                        break;
                    }
                }
            }

            if (!alreadyThere) {
                try (BufferedWriter bw = Files.newBufferedWriter(EMPLOYEE_SAV_CSV, StandardOpenOption.APPEND)) {
                    bw.write(employeeID + "," + savingsID + "," + String.format("%.1f", balance));
                    bw.newLine();
                }
            }

        } catch (IOException e) {
            System.out.println("Warning: Could not update EmployeeSavings.csv: " + e.getMessage());
        }
    }

    // -------------------------------------------------
    // Update EmployeeAccount column (index 14) in customerInfo.csv
    // -------------------------------------------------

    private void updateCustomerInfoCSV(String employeeID, boolean hasAccount) {
        if (!Files.exists(CUSTOMER_INFO_CSV)) return;

        try {
            List<String> lines = Files.readAllLines(CUSTOMER_INFO_CSV);
            if (lines.isEmpty()) return;

            for (int i = 1; i < lines.size(); i++) {
                String[] fields = lines.get(i).split(",", -1);
                if (fields.length > 0 && fields[0].trim().equals(employeeID)) {
                    // Column 14 = EmployeeAccount
                    if (fields.length > 14) {
                        fields[14] = hasAccount ? "true" : "false";
                        lines.set(i, String.join(",", fields));
                    }
                    break;
                }
            }

            Files.write(CUSTOMER_INFO_CSV, lines,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            System.out.println("Warning: Could not update customerInfo.csv: " + e.getMessage());
        }
    }

    // -------------------------------------------------
    // Getters
    // -------------------------------------------------

    public String getEmployeeID()              { return employeeID; }
    public String getName()                    { return name; }
    public BankingCSV.User getBankingUser()    { return bankingUser; }
    public DebitCard getDebitCard()            { return debitCard; }
    public CreditCard getCreditCard()          { return creditCard; }
    public SavingsAccount getSavingsAccount()  { return savingsAccount; }

    // -------------------------------------------------
    // showAccounts: print full account summary
    // -------------------------------------------------

    public void showAccounts() {
        System.out.println("============================================");
        System.out.println("Employee: " + name + " | ID: " + employeeID);
        System.out.println("============================================");

        System.out.println("\n--- Checking Account ---");
        bankingUser.printAccounts();

        System.out.println("\n--- Savings Account ---");
        if (savingsAccount != null) {
            System.out.println("Savings ID:      " + savingsAccount.getSavingsID());
            System.out.printf( "Savings Balance: $%.2f%n", savingsAccount.getSavings());
        } else {
            System.out.println("No savings account found.");
        }

        System.out.println("\n--- Debit Card ---");
        debitCard.displayFeeSchedule();

        System.out.println("\n--- Credit Card ---");
        creditCard.display();

        System.out.println("============================================");
    }
}
