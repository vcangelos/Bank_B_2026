import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.io.IOException;

public class PersonalLoan {

    private double principal;
    private double interestRate;
    private int loanTerm; // months
    private String loanType;
    private String loanId;
    private LocalDate startDate;

    private int creditScore;
    private boolean approved;

    private double totalPaid;
    private double remainingBalance;
    private int paymentsMade;

    private boolean autoPayEnabled;

    public PersonalLoan(double principal, int loanTerm, String loanType, int creditScore, boolean autoPayEnabled) {
        if (principal <= 0)                          throw new IllegalArgumentException("Principal must be > 0.");
        if (loanTerm < 1 || loanTerm > 60)           throw new IllegalArgumentException("Term must be 1-60 months.");
        if (loanType == null || loanType.isBlank())  throw new IllegalArgumentException("Loan type cannot be blank.");

        this.principal       = roundMoney(principal);
        this.loanTerm        = loanTerm;
        this.loanType        = loanType;
        this.loanId          = generateLoanId();
        this.startDate       = LocalDate.now();
        this.autoPayEnabled  = autoPayEnabled;
        this.creditScore     = creditScore;
        evaluateLoan();
        this.totalPaid        = 0.0;
        this.paymentsMade     = 0;
        this.remainingBalance = approved ? roundMoney(calculateTotalRepayment()) : 0.0;
    }

    private String generateLoanId() {
        return "LN" + (10000 + new Random().nextInt(90000));
    }

    private double roundMoney(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }

    private void evaluateLoan() {
        if      (creditScore >= 750) { approved = true;  interestRate = 0.08; }
        else if (creditScore >= 700) { approved = true;  interestRate = 0.12; }
        else if (creditScore >= 650) { approved = true;  interestRate = 0.16; }
        else if (creditScore >= 600) { approved = true;  interestRate = 0.20; }
        else                         { approved = false; interestRate = 0.0;  }
    }

    public double calculateMonthlyPayment() {
        if (!approved) return 0.0;
        double monthlyRate = interestRate / 12.0;
        if (monthlyRate == 0) return roundMoney(principal / loanTerm);
        return roundMoney((principal * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -loanTerm)));
    }

    public double calculateTotalRepayment() {
        return approved ? roundMoney(calculateMonthlyPayment() * loanTerm) : 0.0;
    }

    public double calculateTotalInterest() {
        return approved ? roundMoney(calculateTotalRepayment() - principal) : 0.0;
    }

    public boolean isPaidOff() { return remainingBalance <= 0.0; }

    public double getCurrentMonthlyDue() {
        if (!approved || isPaidOff()) return 0.0;
        return roundMoney(Math.min(calculateMonthlyPayment(), remainingBalance));
    }

    private boolean applyLoanPayment(double amount) {
        if (!approved)    { System.out.println("  Loan not approved.");       return false; }
        if (isPaidOff())  { System.out.println("  Loan already paid off.");   return false; }
        if (amount <= 0)  { System.out.println("  Payment must be > $0.");    return false; }
        double applied    = roundMoney(Math.min(amount, remainingBalance));
        totalPaid         = roundMoney(totalPaid + applied);
        remainingBalance  = roundMoney(remainingBalance - applied);
        paymentsMade++;
        System.out.printf("  Payment applied: $%.2f  |  Remaining: $%.2f%n", applied, remainingBalance);
        return true;
    }

    public boolean payFromChecking(CheckingAccount.Account checking, double amount) {
        if (checking == null || !checking.isActive) { System.out.println("  Checking account unavailable."); return false; }
        if (checking.balance < amount) { System.out.println("  Insufficient funds in checking."); return false; }
        checking.balance = roundMoney(checking.balance - amount);
        checking.addTransaction("Personal Loan Payment", amount);
        checking.updateFlags();
        return applyLoanPayment(amount);
    }

    public boolean payFromSavings(SavingsAccount savings, double amount) throws IOException {
        if (savings == null) { System.out.println("  No savings account."); return false; }
        if (savings.getSavings() < amount) { System.out.println("  Insufficient funds in savings."); return false; }
        if (!savings.withdrawSavings(amount)) return false;
        savings.update();
        return applyLoanPayment(amount);
    }

    public boolean payMonthlyFromChecking(CheckingAccount.Account checking) {
        return payFromChecking(checking, getCurrentMonthlyDue());
    }

    public boolean payMonthlyFromSavings(SavingsAccount savings) throws IOException {
        return payFromSavings(savings, getCurrentMonthlyDue());
    }

    // ── Getters / Setters ─────────────────────────────────────────────────
    public double  getPrincipal()       { return principal; }
    public double  getInterestRate()    { return interestRate; }
    public int     getLoanTerm()        { return loanTerm; }
    public String  getLoanType()        { return loanType; }
    public String  getLoanId()          { return loanId; }
    public boolean isApproved()         { return approved; }
    public double  getRemainingBalance(){ return remainingBalance; }
    public double  getTotalPaid()       { return totalPaid; }
    public int     getPaymentsMade()    { return paymentsMade; }
    public boolean isAutoPayEnabled()   { return autoPayEnabled; }
    public void    setAutoPayEnabled(boolean v) { autoPayEnabled = v; }

    public void display() {
        System.out.println("\n  ─── Personal Loan ───────────────────────");
        System.out.println("  Loan ID:       " + loanId);
        System.out.println("  Type:          " + loanType);
        System.out.printf ("  Principal:     $%.2f%n", principal);
        System.out.println("  Term:          " + loanTerm + " months");
        System.out.println("  Status:        " + (approved ? "APPROVED" : "DENIED"));
        if (approved) {
            System.out.printf("  Rate:          %.2f%%%n", interestRate * 100);
            System.out.printf("  Monthly Pmt:   $%.2f%n", calculateMonthlyPayment());
            System.out.printf("  Remaining:     $%.2f%n", remainingBalance);
            System.out.printf("  Total Paid:    $%.2f%n", totalPaid);
            System.out.println("  AutoPay:       " + (autoPayEnabled ? "Yes" : "No"));
        }
    }

    public void saveToCSV() {
        String fileName = "personalloan.csv";
        boolean exists = new java.io.File(fileName).exists();
        try (java.io.FileWriter fw = new java.io.FileWriter(fileName, true)) {
            if (!exists) fw.write("LoanID,LoanType,Principal,RemainingBalance,TotalPaid,PaymentsMade,InterestRate,Approved,AutoPay,StartDate\n");
            fw.write(loanId + "," + loanType + "," + principal + "," + remainingBalance + "," +
                    totalPaid + "," + paymentsMade + "," + interestRate + "," + approved + "," +
                    autoPayEnabled + "," + startDate + "\n");
        } catch (IOException e) {
            System.out.println("  Error saving loan: " + e.getMessage());
        }
    }

    // ── Launch (called by LoansModule) ────────────────────────────────────
    public static void launch(Scanner sc, User appUser,
                              List<CheckingAccount.Account> checkingAccounts,
                              SavingsAccount savingsAcc,
                              List<CheckingAccount.CheckingUser> checkingUsersRef) throws IOException {

        System.out.println("\n  ─── Personal Loan Application ───────────");
        System.out.print("  Loan amount: $");
        double amount;
        try { amount = Double.parseDouble(sc.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("  Invalid amount."); return; }

        System.out.print("  Term in months (1-60): ");
        int term;
        try { term = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("  Invalid term."); return; }

        System.out.print("  Loan type (e.g. Medical, Auto, Home Improvement): ");
        String type = sc.nextLine().trim();
        if (type.isEmpty()) { System.out.println("  Loan type cannot be blank."); return; }

        PersonalLoan loan;
        try {
            loan = new PersonalLoan(amount, term, type, appUser.creditScore, false);
        } catch (IllegalArgumentException e) {
            System.out.println("  Error: " + e.getMessage());
            return;
        }

        loan.display();
        if (!loan.isApproved()) { System.out.println("  Loan denied. Credit score too low."); return; }

        loan.saveToCSV();

        boolean running = true;
        while (running) {
            System.out.println("\n────────────────────────────────────────");
            System.out.println("BANK  |  Personal Loan");
            System.out.println("────────────────────────────────────────");
            loan.display();
            System.out.println("  [1] Pay Monthly (Checking)");
            System.out.println("  [2] Pay Monthly (Savings)");
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
                    if (loan.payFromChecking(acc, loan.getCurrentMonthlyDue())) {
                        CheckingAccount.writeCSV("checking_accounts.csv", checkingUsersRef);
                        appUser.checkingAccount = acc.balance;
                        loan.saveToCSV();
                    }
                }
                case "2" -> {
                    if (savingsAcc == null) { System.out.println("  No savings account."); break; }
                    if (loan.payFromSavings(savingsAcc, loan.getCurrentMonthlyDue())) {
                        appUser.savingsAccount = savingsAcc.getSavings();
                        loan.saveToCSV();
                    }
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
}