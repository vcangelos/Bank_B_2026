// BUG FIX: removed "package Bank;" — this file lives in the default package with the rest of the app.

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class HomeEquityLoan {

    private double appraisedValue;
    private double currentMortgageBalance;
    private double loanAmount;
    private double availableEquity;

    private static final double INTEREST_RATE = 0.03;
    private static final double BANK_FEES     = 400.0;

    private int    termMonths;
    private double monthlyPayment;
    private double remainingBalance;

    private String accountType;
    private int    paymentsMade;

    // BUG FIX: was CheckingAccount.User — renamed to CheckingAccount.CheckingUser
    private CheckingAccount.CheckingUser user;
    private String accountID;
    // BUG FIX: was CheckingAccount.SavingsAccount — using our SavingsAccount class directly
    private SavingsAccount savingsAccount;

    public HomeEquityLoan(double appraisedValue,
                          double currentMortgageBalance,
                          double loanAmount,
                          int termMonths,
                          CheckingAccount.CheckingUser user,
                          String accountID,
                          SavingsAccount savingsAccount,
                          String accountType) {
        this.appraisedValue        = appraisedValue;
        this.currentMortgageBalance= currentMortgageBalance;
        this.loanAmount            = loanAmount;
        this.termMonths            = termMonths;
        this.user                  = user;
        this.accountID             = accountID;
        this.savingsAccount        = savingsAccount;
        this.accountType           = accountType;
        this.availableEquity       = appraisedValue - currentMortgageBalance;
        this.remainingBalance      = loanAmount + BANK_FEES;
        this.paymentsMade          = 0;
    }

    public boolean isApproved() {
        return appraisedValue > 0 &&
                currentMortgageBalance >= 0 &&
                loanAmount > 0 &&
                loanAmount <= availableEquity &&
                termMonths > 0;
    }

    public void calculateMonthlyPayment() {
        double monthlyRate = INTEREST_RATE / 12;
        monthlyPayment = (remainingBalance * monthlyRate) /
                (1 - Math.pow(1 + monthlyRate, -termMonths));
    }

    public void makePayment() throws IOException {
        if (remainingBalance <= 0) { System.out.println("  Loan already paid off."); return; }

        boolean success = false;

        if (accountType.equalsIgnoreCase("checking") && user != null) {
            for (CheckingAccount.Account acc : user.accounts) {
                if (acc.accountID.equals(accountID) && acc.balance >= monthlyPayment) {
                    user.withdraw(accountID, monthlyPayment);
                    success = true;
                    break;
                }
            }
        } else if (accountType.equalsIgnoreCase("savings") && savingsAccount != null) {
            if (savingsAccount.getSavings() >= monthlyPayment) {
                savingsAccount.withdrawSavings(monthlyPayment);
                savingsAccount.update();
                success = true;
            }
        }

        if (!success) { System.out.println("  Payment failed — insufficient funds."); return; }

        double interest  = remainingBalance * (INTEREST_RATE / 12);
        double principal = monthlyPayment - interest;
        remainingBalance -= principal;
        paymentsMade++;

        System.out.printf("  Payment #%d made. Remaining balance: $%.2f%n", paymentsMade, remainingBalance);
        saveToCSV();
    }

    public void saveToCSV() {
        try {
            File file = new File("Home.csv");
            boolean fileExists = file.exists();
            try (FileWriter fw = new FileWriter(file, true)) {
                if (!fileExists || file.length() == 0) {
                    fw.write("AppraisedValue,CurrentMortgageBalance,LoanAmount,TermMonths,RemainingBalance,PaymentsMade,AccountType\n");
                }
                fw.write(appraisedValue + "," + currentMortgageBalance + "," + loanAmount + "," +
                        termMonths + "," + remainingBalance + "," + paymentsMade + "," + accountType + "\n");
            }
        } catch (IOException e) {
            System.out.println("  Error saving to CSV: " + e.getMessage());
        }
    }

    public void display() {
        System.out.printf("  Appraised Value:     $%.2f%n", appraisedValue);
        System.out.printf("  Mortgage Balance:    $%.2f%n", currentMortgageBalance);
        System.out.printf("  Available Equity:    $%.2f%n", availableEquity);
        System.out.printf("  Loan Amount:         $%.2f%n", loanAmount);
        System.out.printf("  Bank Fees:           $%.2f%n", BANK_FEES);
        System.out.printf("  Remaining Balance:   $%.2f%n", remainingBalance);
        System.out.printf("  Monthly Payment:     $%.2f%n", monthlyPayment);
        System.out.printf("  Term:                %d months%n", termMonths);
        System.out.printf("  Interest Rate:       %.2f%%%n", INTEREST_RATE * 100);
        System.out.printf("  Payments Made:       %d%n", paymentsMade);
        System.out.println("  Pay From:            " + accountType);
    }

    // ── Launch (called by LoansModule) ────────────────────────────────────
    public static void launch(Scanner sc, User appUser,
                              List<CheckingAccount.Account> checkingAccounts,
                              SavingsAccount savingsAcc,
                              List<CheckingAccount.CheckingUser> checkingUsersRef) throws IOException {

        System.out.println("\n  ─── Home Equity Loan Application ────────");

        double appraisedValue;
        double mortgageBalance;
        double loanAmount;
        int    termMonths;

        try {
            System.out.print("  Appraised home value: $");
            appraisedValue  = Double.parseDouble(sc.nextLine().trim());
            System.out.print("  Current mortgage balance: $");
            mortgageBalance = Double.parseDouble(sc.nextLine().trim());
            System.out.print("  Loan amount requested: $");
            loanAmount      = Double.parseDouble(sc.nextLine().trim());
            System.out.print("  Term in months: ");
            termMonths      = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  Invalid input."); return;
        }

        // Determine payment source
        String accountType;
        String accountID = "";
        CheckingAccount.Account chosenChecking = null;
        while (true) {
            System.out.println("  Pay from: [1] Checking  [2] Savings");
            System.out.print("  Select: ");
            String src = sc.nextLine().trim();
            if (src.equals("1")) {
                if (checkingAccounts.isEmpty()) { System.out.println("  No checking accounts."); return; }
                chosenChecking = LoansModule.pickAccount(sc, checkingAccounts);
                if (chosenChecking == null) return;
                accountID   = chosenChecking.accountID;
                accountType = "checking";
                break;
            } else if (src.equals("2")) {
                if (savingsAcc == null) { System.out.println("  No savings account."); return; }
                accountType = "savings";
                break;
            } else {
                System.out.println("  Enter 1 or 2.");
            }
        }

        // Find the CheckingUser for this appUser (needed by HomeEquityLoan constructor)
        CheckingAccount.CheckingUser checkUser = CheckingAccount.findUser(checkingUsersRef, appUser.customerID);

        HomeEquityLoan loan = new HomeEquityLoan(
                appraisedValue, mortgageBalance, loanAmount, termMonths,
                checkUser, accountID, savingsAcc, accountType);

        loan.calculateMonthlyPayment();

        if (!loan.isApproved()) {
            System.out.println("  Loan not approved. Check appraised value and equity.");
            return;
        }

        loan.display();
        loan.saveToCSV();

        boolean running = true;
        while (running) {
            System.out.println("\n────────────────────────────────────────");
            System.out.println("BANK  |  Home Equity Loan");
            System.out.println("────────────────────────────────────────");
            loan.display();
            System.out.println("  [1] Make Monthly Payment");
            System.out.println("  [0] Back");
            System.out.println("────────────────────────────────────────");
            System.out.print("  Select: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    loan.makePayment();
                    if (accountType.equals("checking") && chosenChecking != null) {
                        CheckingAccount.writeCSV("checking_accounts.csv", checkingUsersRef);
                        appUser.checkingAccount = chosenChecking.balance;
                    } else if (accountType.equals("savings") && savingsAcc != null) {
                        appUser.savingsAccount = savingsAcc.getSavings();
                    }
                }
                case "0" -> running = false;
                default  -> System.out.println("  Invalid option.");
            }
        }
    }
}