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
    
    // CSV paths
    private static final Path CUSTOMER_CSV_PATH = Path.of("customerInfo.csv");
    private static final Path SAVINGS_CSV_PATH = Path.of("Savings.csv");
    
    // Reference to checking/savings systems
    private static List<BankingCSV.User> bankingUsers;
    
    // Store active CDs
    private static Map<String, CertificateOfDeposit> activeCDs = new HashMap<>();
    
    // CD counter for generating unique IDs
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
    
    // Main banking system provides access to checking/savings accounts
    public static void setBankingUsers(List<BankingCSV.User> users) {
        bankingUsers = users;
    }
    
    // Generate unique CD ID
    private static String generateCDID() {
        String id = "CD" + String.format("%06d", nextCDNumber);
        nextCDNumber++;
        return id;
    }
    
    // Update customerInfo.csv with CD balance and interest rate
    private static void updateCustomerCDInfo(String customerID, double cdBalance, double cdInterestRate) throws IOException {
        List<String> lines = Files.readAllLines(CUSTOMER_CSV_PATH);
        
        if (lines.isEmpty()) {
            System.out.println("Error: customerInfo.csv is empty");
            return;
        }
        
        // Find and update the customer's row
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] fields = line.split(",", -1);
            
            if (fields.length > 0 && fields[0].trim().equals(customerID)) {
                // Update cdBalance (index 15) and cdInterestRate (index 16)
                if (fields.length > 16) {
                    fields[15] = String.valueOf(cdBalance);
                    fields[16] = String.valueOf(cdInterestRate);
                }
                
                lines.set(i, String.join(",", fields));
                break;
            }
        }
        
        Files.write(CUSTOMER_CSV_PATH, lines);
    }
    
    // Update Savings.csv with new balance
    private static void updateSavingsBalance(String userID, String savingsID, double newBalance) throws IOException {
        List<String> lines = Files.readAllLines(SAVINGS_CSV_PATH);
        
        // Update the matching line
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] fields = line.split(",");
            
            if (fields.length > 0 && fields[0].trim().equals(userID)) {
                lines.set(i, userID + "," + savingsID + "," + newBalance);
                break;
            }
        }
        
        Files.write(SAVINGS_CSV_PATH, lines);
    }
    
    // CHECKLIST: Welcome Screen
    public static void welcomeScreen(Scanner scanner, String customerID) {
        try {
            // Print greeting message
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
            System.out.println();
            System.out.println("A Certificate of Deposit allows you to invest");
            System.out.println("your money for a fixed term at a higher interest");
            System.out.println("rate. Upon maturity, your earnings will be");
            System.out.println("transferred to your Savings Account.");
            System.out.println("========================================");
            
            // Loop for creating multiple CDs
            boolean continueCreating = true;
            
            while (continueCreating) {
                // Query if user wishes to create a CD
                System.out.print("\nWould you like to create a Certificate of Deposit? (yes/no): ");
                String response = scanner.next().toLowerCase();
                
                if (response.equals("yes") || response.equals("y")) {
                    // Go to CD Interface
                    CertificateOfDeposit cd = cdInterface(scanner, customerID);
                    
                    if (cd != null) {
                        // Store the CD
                        activeCDs.put(cd.getCdID(), cd);
                        
                        // Offer option to create another CD
                        System.out.print("\nWould you like to create another CD? (yes/no): ");
                        String another = scanner.next().toLowerCase();
                        
                        if (!another.equals("yes") && !another.equals("y")) {
                            continueCreating = false;
                        }
                    } else {
                        // CD creation failed, ask if they want to try again
                        System.out.print("\nWould you like to try again? (yes/no): ");
                        String retry = scanner.next().toLowerCase();
                        
                        if (!retry.equals("yes") && !retry.equals("y")) {
                            continueCreating = false;
                        }
                    }
                } else {
                    // Return to main menu
                    System.out.println("Returning to main menu...");
                    continueCreating = false;
                }
            }
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // CHECKLIST: CD Interface
    private static CertificateOfDeposit cdInterface(Scanner scanner, String customerID) {
        try {
            System.out.println("\n=== Create Certificate of Deposit ===");
            
            // Check if customer has a checking account
            BankingCSV.User user = BankingCSV.findUser(bankingUsers, customerID);
            
            if (user == null || user.accounts.isEmpty()) {
                System.out.println("Error: You must have a checking account to create a CD.");
                return null;
            }
            
            // Check if customer has a savings account
            if (!SavingsAccount.userIDExists(customerID)) {
                System.out.println("Error: You must have a savings account to create a CD.");
                System.out.println("CD funds will be transferred to savings upon maturity.");
                return null;
            }
            
            // Offer selection of term lengths and interest rates
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
            
            // Prompt for deposit amount (must be >= $1000)
            System.out.print("\nEnter deposit amount (minimum $1,000): $");
            double amount = scanner.nextDouble();
            
            // Validate amount
            if (amount < MIN_DEPOSIT) {
                System.out.println("Error: The amount is less than $1,000. Minimum deposit is $" + String.format("%.2f", MIN_DEPOSIT));
                return null;
            }
            
            // Check if customer has enough in checking account
            String checkingAccountID = user.accounts.get(0).accountID;
            double checkingBalance = user.accounts.get(0).balance;
            
            if (amount > checkingBalance) {
                System.out.println("Error: Insufficient funds in checking account.");
                System.out.println("Available balance: $" + String.format("%.2f", checkingBalance));
                System.out.println("Required: $" + String.format("%.2f", amount));
                return null;
            }
            
            // Withdraw from checking account
            System.out.println("\nWithdrawing $" + String.format("%.2f", amount) + " from checking account...");
            user.withdraw(checkingAccountID, amount);
            
            // Generate CD ID and create CD
            String cdID = generateCDID();
            CertificateOfDeposit newCD = new CertificateOfDeposit(cdID, customerID, amount, interestRate, termMonths);
            
            // Update customerInfo.csv
            updateCustomerCDInfo(customerID, amount, interestRate);
            
            System.out.println("\n✓ Certificate of Deposit created successfully!");
            newCD.displayInfo();
            
            return newCD;
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
    
    // CHECKLIST: Prompt if user wishes to withdraw (before maturation)
    public static void manageCD(Scanner scanner, String customerID) {
        // Show all active CDs for this customer
        System.out.println("\n=== Your Certificates of Deposit ===");
        
        boolean hasActiveCDs = false;
        for (CertificateOfDeposit cd : activeCDs.values()) {
            if (cd.getCustomerID().equals(customerID) && !cd.isClosed()) {
                cd.displayInfo();
                hasActiveCDs = true;
            }
        }
        
        if (!hasActiveCDs) {
            System.out.println("You have no active CDs.");
            return;
        }
        
        // Prompt if user wishes to withdraw money
        System.out.print("\nDo you wish to withdraw money from a CD? (yes/no): ");
        String response = scanner.next().toLowerCase();
        
        if (!response.equals("yes") && !response.equals("y")) {
            System.out.println("Returning to menu...");
            return;
        }
        
        // Get CD ID
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
        
        // Check maturity status
        cd.checkMaturity();
        
        if (cd.isMatured()) {
            // CD has matured - no penalty
            System.out.println("\nYour CD has matured! No penalty will be applied.");
            cd.withdrawAtMaturity();
        } else {
            // CD has NOT matured - warn about penalty
            System.out.println("\n⚠ WARNING: Your CD has not yet matured.");
            System.out.println("Maturity Date: " + cd.getMaturityDate());
            System.out.println("If you withdraw now, a 10% penalty will be applied.");
            
            System.out.print("\nAre you sure you want to withdraw early? (yes/no): ");
            String confirm = scanner.next().toLowerCase();
            
            if (confirm.equals("yes") || confirm.equals("y")) {
                // Calculate penalty and withdraw
                cd.withdrawEarly();
            } else {
                System.out.println("Withdrawal cancelled. Your CD remains active.");
            }
        }
    }
    
    public String getCdID() {
        return cdID;
    }
    
    public String getCustomerID() {
        return customerID;
    }
    
    public double getPrincipal() {
        return principal;
    }
    
    public LocalDate getMaturityDate() {
        return maturityDate;
    }
    
    public boolean isMatured() {
        return isMatured;
    }
    
    public boolean isClosed() {
        return isClosed;
    }
    
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
    
    // Transfer to savings account
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
    
    // CHECKLIST: Withdraw before maturation - calculate penalty and cancel CD
    public double withdrawEarly() {
        if (isClosed) {
            System.out.println("Error: CD is already closed");
            return 0.0;
        }
        
        if (isMatured) {
            return withdrawAtMaturity();
        }
        
        // Calculate penalty using penalty formula (10%)
        double penalty = principal * EARLY_WITHDRAWAL_PENALTY_RATE;
        double amountReturned = principal - penalty;
        
        System.out.println("\n=== Early Withdrawal Summary ===");
        System.out.println("Original principal: $" + String.format("%.2f", principal));
        System.out.println("Penalty (10%): $" + String.format("%.2f", penalty));
        System.out.println("Amount returned: $" + String.format("%.2f", amountReturned));
        System.out.println("CD Status: CANCELLED");
        
        isClosed = true;
        
        // Transfer to savings account
        depositToSavings(amountReturned);
        
        // Clear CD info from customerInfo.csv
        try {
            updateCustomerCDInfo(customerID, 0.0, 0.0);
        } catch (IOException e) {
            System.out.println("Warning: Could not update customerInfo.csv");
        }
        
        return amountReturned;
    }
    
    // CHECKLIST: Didn't withdraw before maturation - notify, calculate/display, transfer to savings
    public double withdrawAtMaturity() {
        if (isClosed) {
            System.out.println("Error: CD is already closed");
            return 0.0;
        }
        
        if (!isMatured) {
            System.out.println("Warning: CD has not matured yet. Early withdrawal penalty will apply.");
            return withdrawEarly();
        }
        
        double finalAmount = calculateMaturityValue();
        
        // Notify user that CD has matured
        System.out.println("\n🎉 CONGRATULATIONS! Your CD has matured! finally");
        System.out.println("\n=== Final Worth ===");
        System.out.println("Original deposit: $" + String.format("%.2f", principal));
        System.out.println("Interest earned: $" + String.format("%.2f", calculateInterest()));
        System.out.println("Total value: $" + String.format("%.2f", finalAmount));
        
        isClosed = true;
        
        // Transfer funds to Savings Account
        System.out.println("\nTransferring funds to your Savings Account...");
        depositToSavings(finalAmount);
        
        // Clear CD info from customerInfo.csv
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