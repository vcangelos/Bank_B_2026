import java.util.UUID;
import java.util.List;
import java.util.Scanner;
import java.nio.file.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class StudentLoan {

    private String loanID;
    private double principal;
    private double interestRate;
    private int termMonths;
    private boolean isAutoPayEnabled;
    private int creditScore;
    private static final Path StudentLoanCSV = Path.of("StudentLoanList.csv");

    public StudentLoan(String loanID, double principal, double interestRate,
                       int termMonths, int creditScore, boolean isAutoPayEnabled) {
        this.loanID           = loanID;
        this.principal        = principal;
        this.interestRate     = interestRate;
        this.termMonths       = termMonths;
        this.isAutoPayEnabled = isAutoPayEnabled;
        this.creditScore      = creditScore;
    }

    // ── Factory ───────────────────────────────────────────────────────────
    public static StudentLoan createStudentLoan(String userID, double amount, int creditScore) {
        if (amount < 500) {
            System.out.println("  Loan must be at least $500.");
            return null;
        }
        int   score  = clampCreditScore(creditScore);
        double rate  = getInterestRate(score);
        String id    = generateUniqueLoanID();
        StudentLoan loan = new StudentLoan(id, amount, rate, 120, score, false);
        System.out.println("  Loan created. ID: " + id + "  Rate: " + (rate * 100) + "%");
        return loan;
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    public static int clampCreditScore(int score) {
        if (score < 300) return 300;
        if (score > 850) return 850;
        return score;
    }

    public static double getInterestRate(int score) {
        if (score >= 750) return 0.03;
        else if (score >= 700) return 0.05;
        else if (score >= 650) return 0.07;
        else if (score >= 600) return 0.10;
        else return 0.15;
    }

    public static String generateUniqueLoanID() {
        String id;
        do {
            id = "LN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (loanExists(id));
        return id;
    }

    // BUG FIX: was "LoanExists" (capital L) but called as "loanExists"; unified to loanExists.
    // Also added missing return false + IOException catch.
    public static boolean loanExists(String loanID) {
        if (!Files.exists(StudentLoanCSV)) return false;
        try (BufferedReader reader = Files.newBufferedReader(StudentLoanCSV)) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = line.split(",", -1);
                if (cols.length > 0 && cols[0].trim().equals(loanID.trim())) return true;
            }
        } catch (IOException e) { /* file missing = no duplicates */ }
        return false;
    }

    // ── Business logic ────────────────────────────────────────────────────
    public double calculateMonthlyPayment() {
        double monthlyRate = interestRate / 12.0;
        if (monthlyRate == 0) return principal / termMonths;
        return (principal * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -termMonths));
    }

    public void applyInterest() {
        principal += principal * interestRate;
    }

    public void makePayment(double amount) {
        principal -= amount;
        if (principal < 0) principal = 0;
    }

    // ── CSV ───────────────────────────────────────────────────────────────
    public void saveToCSV() throws IOException {
        boolean exists = Files.exists(StudentLoanCSV);
        try (BufferedWriter bw = Files.newBufferedWriter(StudentLoanCSV,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            if (!exists) bw.write("LoanID,Principal,InterestRate,TermMonths,AutoPay\n");
            bw.write(loanID + "," + principal + "," + interestRate + "," + termMonths + "," + isAutoPayEnabled + "\n");
        }
    }

    // ── Display ───────────────────────────────────────────────────────────
    public void display() {
        System.out.println("  Loan ID:       " + loanID);
        System.out.printf ("  Principal:     $%.2f%n", principal);
        System.out.printf ("  Interest Rate: %.2f%%%n", interestRate * 100);
        System.out.println("  Term:          " + termMonths + " months");
        System.out.printf ("  Monthly Pmt:   $%.2f%n", calculateMonthlyPayment());
        System.out.println("  AutoPay:       " + (isAutoPayEnabled ? "Yes" : "No"));
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public String getLoanID()        { return loanID; }
    public double getPrincipal()     { return principal; }
    public double getInterestRate()  { return interestRate; }
    public int    getTermMonths()    { return termMonths; }
    public boolean isAutoPayEnabled(){ return isAutoPayEnabled; }
    public void setAutoPayEnabled(boolean v) { this.isAutoPayEnabled = v; }

    // ── Launch (called by LoansModule) ────────────────────────────────────
    public static void launch(Scanner sc, User appUser,
                              List<CheckingAccount.Account> checkingAccounts,
                              SavingsAccount savingsAcc,
                              List<CheckingAccount.CheckingUser> checkingUsersRef) throws IOException {

        // Load or create loan
        StudentLoan loan = loadExistingLoan(appUser.customerID);
        if (loan == null) {
            System.out.println("\n  No student loan on file. Apply for one?");
            System.out.print("  Loan amount (min $500): $");
            double amount;
            try { amount = Double.parseDouble(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("  Invalid amount."); return; }
            loan = StudentLoan.createStudentLoan(appUser.customerID, amount, appUser.creditScore);
            if (loan == null) return;
            loan.saveToCSV();
        }

        boolean running = true;
        while (running) {
            System.out.println("\n────────────────────────────────────────");
            System.out.println("BANK  |  Student Loan");
            System.out.println("────────────────────────────────────────");
            loan.display();
            System.out.println("  [1] Make Payment (Checking)");
            System.out.println("  [2] Make Payment (Savings)");
            System.out.println("  [3] Toggle AutoPay");
            System.out.println("  [0] Back");
            System.out.println("────────────────────────────────────────");
            System.out.print("  Select: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    if (checkingAccounts.isEmpty()) { System.out.println("  No checking accounts."); break; }
                    CheckingAccount.Account acc = LoansModule.pickAccount(sc, checkingAccounts);
                    if (acc == null) break;
                    double due = loan.calculateMonthlyPayment();
                    if (acc.balance < due) { System.out.println("  Insufficient funds."); break; }
                    acc.balance -= due;
                    acc.addTransaction("Student Loan Payment", due);
                    acc.updateFlags();
                    loan.makePayment(due);
                    CheckingAccount.writeCSV("checking_accounts.csv", checkingUsersRef);
                    appUser.checkingAccount = acc.balance;
                    loan.saveToCSV();
                    System.out.printf("  Paid $%.2f. Remaining principal: $%.2f%n", due, loan.getPrincipal());
                }
                case "2" -> {
                    if (savingsAcc == null) { System.out.println("  No savings account linked."); break; }
                    double due = loan.calculateMonthlyPayment();
                    if (savingsAcc.getSavings() < due) { System.out.println("  Insufficient funds."); break; }
                    savingsAcc.withdrawSavings(due);
                    savingsAcc.update();
                    loan.makePayment(due);
                    appUser.savingsAccount = savingsAcc.getSavings();
                    loan.saveToCSV();
                    System.out.printf("  Paid $%.2f. Remaining principal: $%.2f%n", due, loan.getPrincipal());
                }
                case "3" -> {
                    loan.setAutoPayEnabled(!loan.isAutoPayEnabled());
                    System.out.println("  AutoPay " + (loan.isAutoPayEnabled() ? "enabled." : "disabled."));
                }
                case "0" -> running = false;
                default  -> System.out.println("  Invalid option.");
            }
        }
    }

    private static StudentLoan loadExistingLoan(String userID) {
        // Simple stub — real implementation would match userID column in CSV
        return null;
    }
}