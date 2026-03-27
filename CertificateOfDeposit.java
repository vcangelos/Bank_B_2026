import java.time.LocalDate;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;

public class CertificateOfDeposit {
    
    // CD properties
    private String cdID;
    private String customerID;
    private double principal;
    private double interestRate;
    private int termMonths;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private boolean isMatured;
    private boolean isClosed;
    
    // Constants
    private static final double MIN_DEPOSIT = 1000.00;
    private static final double EARLY_WITHDRAWAL_PENALTY_RATE = 0.10;
    
    // CSV Index Constants (Fixed to hit the correct columns)
    private static final int CSV_CD_BALANCE_IDX = 19;
    private static final int CSV_CD_RATE_IDX = 20;
    private static final int CSV_SAVINGS_BALANCE_IDX = 2; // Assuming Savings is [userid, savingsID, balance]
    
    // CSV paths
    private static final Path CUSTOMER_CSV_PATH = Path.of("customerInfo.csv");
    private static final Path SAVINGS_CSV_PATH = Path.of("Savings.csv");
    
    // Reference to checking/savings systems
    private static List<BankingCSV.User> bankingUsers;
    private static Map<String, CertificateOfDeposit> activeCDs = new HashMap<>();
    
    private static int nextCDNumber = 1;
    
    public CertificateOfDeposit(String cdID, String customerID, double principal, double interestRate, int termMonths) {
        this.cdID = cdID;
        this.customerID = customerID;
        this.principal = principal;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.startDate = LocalDate.now();
        this.maturityDate = startDate.plusMonths(termMonths);
        this.isMatured = false;
        this.isClosed = false;
    }
    
    public static void setBankingUsers(List<BankingCSV.User> users) {
        bankingUsers = users;
    }
    
    private static String generateCDID() {
        String id = "CD" + String.format("%06d", nextCDNumber);
        nextCDNumber++;
        return id;
    }
    
    // SYNCHRONIZED and using correct indexes (19 & 20)
    private static synchronized void updateCustomerCDInfo(String customerID, double cdBalance, double cdInterestRate) throws IOException {
        List<String> lines = Files.readAllLines(CUSTOMER_CSV_PATH);
        if (lines.isEmpty()) return;
        
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] fields = line.split(",", -1);
            
            if (fields.length > 0 && fields[0].trim().equals(customerID)) {
                if (fields.length > CSV_CD_RATE_IDX) {
                    fields[CSV_CD_BALANCE_IDX] = String.valueOf(cdBalance);
                    fields[CSV_CD_RATE_IDX] = String.valueOf(cdInterestRate);
                }
                lines.set(i, String.join(",", fields));
                break;
            }
        }
        Files.write(CUSTOMER_CSV_PATH, lines);
    }
    
    // SYNCHRONIZED and safely replaces ONLY the balance to protect the Savings team's extra columns
    private static synchronized void updateSavingsBalance(String userID, String savingsID, double newBalance) throws IOException {
        List<String> lines = Files.readAllLines(SAVINGS_CSV_PATH);
        
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] fields = line.split(",", -1);
            
            if (fields.length > 0 && fields[0].trim().equals(userID)) {
                if (fields.length > CSV_SAVINGS_BALANCE_IDX) {
                    fields[CSV_SAVINGS_BALANCE_IDX] = String.valueOf(newBalance);
                }
                lines.set(i, String.join(",", fields));
                break;
            }
        }
        Files.write(SAVINGS_CSV_PATH, lines);
    }
    
    public static void welcomeScreen(Scanner scanner, String customerID) {
        try {
            csvFile customerFile = new csvFile(CUSTOMER_CSV_PATH);
            Map<String, String> customerRecord = customerFile.getRecord("customerID", customerID);
            
            if (customerRecord == null) {
                System.out.println("Error: Customer not found");
                return;
            }
            
            String firstName = customerRecord.get("firstName");
            String lastName = customerRecord.get("lastName");
            
            System.out.println("\n-----------------------------------------");
            System.out.println("   Certificate of Deposit (CD)");
            System.out.println("------------------------------------------");
            System.out.println("Welcome, " + firstName + " " + lastName + "!");
            System.out.println("\nA Certificate of Deposit allows you to invest");
            System.out.println("your money for a fixed term at a higher interest");
            System.out.println("rate. Upon maturity, your earnings will be");
            System.out.println("transferred to your Savings Account.");
            System.out.println("========================================");
            
            boolean continueCreating = true;
            while (continueCreating) {
                System.out.print("\nWould you like to create a Certificate of Deposit? (yes/no): ");
                String response = scanner.next().toLowerCase();
                
                if (response.equals("yes") || response.equals("y")) {
                    CertificateOfDeposit cd = cdInterface(scanner, customerID);
                    
                    if (cd != null) {
                        activeCDs.put(cd.getCdID(), cd);
                        
                        System.out.print("\nWould you like to create another CD? (yes/no): ");
                        String another = scanner.next().toLowerCase();
                        if (!another.equals("yes") && !another.equals("y")) {
                            continueCreating = false;
                        }
                    } else {
                        System.out.print("\nWould you like to try again? (yes/no): ");
                        String retry = scanner.next().toLowerCase();
                        if (!retry.equals("yes") && !retry.equals("y")) {
                            continueCreating = false;
                        }
                    }
                } else {
                    System.out.println("Returning to main menu...");
                    continueCreating = false;
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static CertificateOfDeposit cdInterface(Scanner scanner, String customerID) {
        if (bankingUsers == null) {
            System.out.println("Error: Banking system not initialized. Call setBankingUsers first.");
            return null;
        }

        try {
            System.out.println("\n=== Create Certificate of Deposit ===");
            
            BankingCSV.User user = BankingCSV.findUser(bankingUsers, customerID);
            if (user == null || user.accounts.isEmpty()) {
                System.out.println("Error: You must have a checking account to create a CD.");
                return null;
            }
            
            if (!SavingsAccount.userIDExists(customerID)) {
                System.out.println("Error: You must have a savings account to create a CD.");
                System.out.println("CD funds will be transferred to savings upon maturity.");
                return null;
            }
            
            System.out.println("\nAvailable CD Terms:");
            System.out.println("1. 6 months  - 3.0% APY");
            System.out.println("2. 12 months - 4.0% APY");
            System.out.println("3. 24 months - 4.5% APY");
            System.out.println("4. 36 months - 5.0% APY");
            System.out.println("5. 60 months - 5.5% APY");
            
            System.out.print("\nSelect term option (1-5): ");
            int choice = scanner.nextInt();
            
            int termMonths;
            double interestRate;
            switch (choice) {
                case 1: termMonths = 6; interestRate = 0.03; break;
                case 2: termMonths = 12; interestRate = 0.04; break;
                case 3: termMonths = 24; interestRate = 0.045; break;
                case 4: termMonths = 36; interestRate = 0.05; break;
                case 5: termMonths = 60; interestRate = 0.055; break;
                default:
                    System.out.println("Invalid selection.");
                    return null;
            }
            
            System.out.print("\nEnter deposit amount (minimum $1,000): $");
            double amount = scanner.nextDouble();
            
            if (amount < MIN_DEPOSIT) {
                System.out.println("Error: The amount is less than $1,000. Minimum deposit is $" + String.format("%.2f", MIN_DEPOSIT));
                return null;
            }
            
            String checkingAccountID = user.accounts.get(0).accountID;
            double checkingBalance = user.accounts.get(0).balance;
            
            if (amount > checkingBalance) {
                System.out.println("Error: Insufficient funds in checking account.");
                System.out.println("Available balance: $" + String.format("%.2f", checkingBalance));
                return null;
            }
            
            System.out.println("\nWithdrawing $" + String.format("%.2f", amount) + " from checking account...");
            user.withdraw(checkingAccountID, amount);
            
            String cdID = generateCDID();
            CertificateOfDeposit newCD = new CertificateOfDeposit(cdID, customerID, amount, interestRate, termMonths);
            
            updateCustomerCDInfo(customerID, amount, interestRate);
            
            System.out.println("\n✓ Certificate of Deposit created successfully!");
            newCD.displayInfo();
            return newCD;
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
    
    public static void manageCD(Scanner scanner, String customerID) {
        System.out.println("\n=== Your Certificates of Deposit ===");
        
        boolean hasActiveCDs = false;
        for (CertificateOfDeposit cd : activeCDs.values()) {
            if (cd.getCustomerID().equals(customerID) && !cd.isClosed()) {
                // Ensure maturity status is up to date before displaying
                cd.checkMaturity();
                cd.displayInfo();
                hasActiveCDs = true;
            }
        }
        
        if (!hasActiveCDs) {
            System.out.println("You have no active CDs.");
            return;
        }
        
        System.out.print("\nDo you wish to withdraw money from a CD? (yes/no): ");
        String response = scanner.next().toLowerCase();
        if (!response.equals("yes") && !response.equals("y")) {
            System.out.println("Returning to menu...");
            return;
        }
        
        System.out.print("Enter CD ID: ");
        String cdID = scanner.next();
        CertificateOfDeposit cd = activeCDs.get(cdID);
        
        if (cd == null || !cd.getCustomerID().equals(customerID)) {
            System.out.println("Error: CD not found.");
            return;
        }
        if (cd.isClosed()) {
            System.out.println("Error: This CD is already closed.");
            return;
        }
        
        // Force a check right before interacting with it
        cd.checkMaturity();
        
        if (cd.isMatured()) {
            System.out.println("\nYour CD has matured! No penalty will be applied.");
            cd.withdrawAtMaturity();
        } else {
            System.out.println("\n⚠ WARNING: Your CD has not yet matured.");
            System.out.println("Maturity Date: " + cd.getMaturityDate());
            System.out.println("If you withdraw now, a 10% penalty will be applied.");
            
            System.out.print("\nAre you sure you want to withdraw early? (yes/no): ");
            String confirm = scanner.next().toLowerCase();
            
            if (confirm.equals("yes") || confirm.equals("y")) {
                cd.withdrawEarly();
            } else {
                System.out.println("Withdrawal cancelled. Your CD remains active.");
            }
        }
    }
    
    public String getCdID() { return cdID; }
    public String getCustomerID() { return customerID; }
    public double getPrincipal() { return principal; }
    public LocalDate getMaturityDate() { return maturityDate; }
    public boolean isMatured() { return isMatured; }
    public boolean isClosed() { return isClosed; }
    
    public double calculateInterest() {
        double years = termMonths / 12.0;
        return principal * interestRate * years;
    }
    
    public double calculateMaturityValue() {
        return principal + calculateInterest();
    }
    
    public void checkMaturity() {
        LocalDate today = LocalDate.now();
        if (today.isAfter(maturityDate) || today.isEqual(maturityDate)) {
            isMatured = true;
        }
    }
    
    private boolean depositToSavings(double amount) {
        try {
            if (!SavingsAccount.userIDExists(customerID)) {
                System.out.println("ERROR: User does not have a savings account.");
                return false;
            }
            
            csvFile savingsFile = new csvFile(SAVINGS_CSV_PATH);
            Map<String, String> record = savingsFile.getRecord("userid", customerID);
            
            if (record == null) {
                System.out.println("ERROR: Could not find savings record.");
                return false;
            }
            
            double currentBalance = Double.parseDouble(record.get("Savings"));
            String savingsID = record.get("SavingsID");
            double newBalance = currentBalance + amount;
            
            updateSavingsBalance(customerID, savingsID, newBalance);
            
            System.out.println("\n✓ Transferred to Savings Account:");
            System.out.println("  Previous balance: $" + String.format("%.2f", currentBalance));
            System.out.println("  Amount added: $" + String.format("%.2f", amount));
            System.out.println("  New balance: $" + String.format("%.2f", newBalance));
            
            return true;
            
        } catch (IOException | NumberFormatException e) {
            System.out.println("ERROR: Failed to transfer to savings: " + e.getMessage());
            return false;
        }
    }
    
    public double withdrawEarly() {
        if (isClosed) {
            System.out.println("Error: CD is already closed");
            return 0.0;
        }
        
        checkMaturity();
        if (isMatured) {
            return withdrawAtMaturity();
        }
        
        double penalty = principal * EARLY_WITHDRAWAL_PENALTY_RATE;
        double amountReturned = principal - penalty;
        
        System.out.println("\n=== Early Withdrawal Summary ===");
        System.out.println("Original principal: $" + String.format("%.2f", principal));
        System.out.println("Penalty (10%): $" + String.format("%.2f", penalty));
        System.out.println("Amount returned: $" + String.format("%.2f", amountReturned));
        System.out.println("CD Status: CANCELLED");
        
        isClosed = true;
        depositToSavings(amountReturned);
        
        try {
            updateCustomerCDInfo(customerID, 0.0, 0.0);
        } catch (IOException e) {
            System.out.println("Warning: Could not update customerInfo.csv");
        }
        
        return amountReturned;
    }
    
    public double withdrawAtMaturity() {
        if (isClosed) {
            System.out.println("Error: CD is already closed");
            return 0.0;
        }
        
        checkMaturity(); // Ensure state is up to date
        if (!isMatured) {
            System.out.println("Warning: CD has not matured yet. Early withdrawal penalty will apply.");
            return withdrawEarly();
        }
        
        double finalAmount = calculateMaturityValue();
        
        System.out.println("\n your CD has matured");
        System.out.println("\n=== Final Worth ===");
        System.out.println("Original deposit: $" + String.format("%.2f", principal));
        System.out.println("Interest earned: $" + String.format("%.2f", calculateInterest()));
        System.out.println("Total value: $" + String.format("%.2f", finalAmount));
        
        isClosed = true;
        
        System.out.println("\nTransferring funds to your Savings Account...");
        depositToSavings(finalAmount);
        
        try {
            updateCustomerCDInfo(customerID, 0.0, 0.0);
        } catch (IOException e) {
            System.out.println("Warning: Could not update customerInfo.csv");
        }
        
        return finalAmount;
    }
    
    public void displayInfo() {
        System.out.println("\n=== Certificate of Deposit Details ===");
        System.out.println("CD ID: " + cdID);
        System.out.println("Customer ID: " + customerID);
        System.out.println("Principal: $" + String.format("%.2f", principal));
        System.out.println("Interest Rate: " + (interestRate * 100) + "% APY");
        System.out.println("Term: " + termMonths + " months");
        System.out.println("Start Date: " + startDate);
        System.out.println("Maturity Date: " + maturityDate);
        System.out.println("Status: " + (isMatured ? "MATURED" : "Active"));
        System.out.println("Expected Value at Maturity: $" + String.format("%.2f", calculateMaturityValue()));
        
        if (!isClosed) {
            long daysUntilMaturity = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), maturityDate);
            if (daysUntilMaturity > 0) {
                System.out.println("Days until maturity: " + daysUntilMaturity);
            }
        }
    }
    
    public static boolean isValidDeposit(double amount) {
        return amount >= MIN_DEPOSIT;
    }
}
