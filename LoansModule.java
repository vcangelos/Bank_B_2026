import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * LoansModule is the single entry point called by menu.java for the Loans dashboard.
 * It loads the user's checking/savings accounts once, then routes to the right loan type.
 * All loan classes (PersonalLoan, StudentLoan, MortgageLoan, HomeEquityLoan) call
 * LoansModule.pickAccount() for account selection so the logic lives in one place.
 */
public class LoansModule {

    // ── Called by menu.launchModule("Loans") ─────────────────────────────
    public static void launch(Scanner sc, User appUser) throws IOException {

        // Load checking accounts once — all sub-modules share this list so balance
        // changes made in one loan are visible to the next without re-reading the CSV.
        List<CheckingAccount.Account> checkingAccounts = new ArrayList<>();
        List<CheckingAccount.CheckingUser> checkingUsersRef = new ArrayList<>();
        try {
            checkingUsersRef = CheckingAccount.readCSV("checking_accounts.csv");
            CheckingAccount.CheckingUser cu = CheckingAccount.findUser(checkingUsersRef, appUser.customerID);
            if (cu != null) checkingAccounts = cu.accounts;
        } catch (IOException e) {
            System.out.println("  Note: Could not load checking accounts.");
        }

        // Load savings account for this user
        SavingsAccount savingsAcc = null;
        try {
            savingsAcc = SavingsAccount.OpenSavingsAccount(appUser.customerID);
        } catch (IOException e) {
            System.out.println("  Note: Could not load savings account.");
        }

        final List<CheckingAccount.CheckingUser> usersRef = checkingUsersRef;
        final SavingsAccount savings = savingsAcc;
        final List<CheckingAccount.Account> checking = checkingAccounts;

        boolean running = true;
        while (running) {
            System.out.println("\n────────────────────────────────────────");
            System.out.println("BANK  |  Loans");
            System.out.println("────────────────────────────────────────");
            System.out.println("  [1] Personal Loan");
            System.out.println("  [2] Student Loan");
            System.out.println("  [3] Mortgage");
            System.out.println("  [4] Home Equity Loan");
            System.out.println("  [0] Back to Dashboard");
            System.out.println("────────────────────────────────────────");
            System.out.print("  Select: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> PersonalLoan.launch(sc, appUser, checking, savings, usersRef);
                case "2" -> StudentLoan.launch(sc, appUser, checking, savings, usersRef);
                case "3" -> MortgageLoan.launch(sc, appUser, checking, savings, usersRef);
                case "4" -> HomeEquityLoan.launch(sc, appUser, checking, savings, usersRef);
                case "0" -> running = false;
                default  -> System.out.println("  Invalid option.");
            }
        }
    }

    // ── Shared account picker used by all loan classes ────────────────────
    public static CheckingAccount.Account pickAccount(Scanner sc, List<CheckingAccount.Account> accounts) {
        List<CheckingAccount.Account> active = new ArrayList<>();
        for (CheckingAccount.Account a : accounts) {
            if (a.isActive) active.add(a);
        }
        if (active.isEmpty()) {
            System.out.println("  No active checking accounts available.");
            return null;
        }
        System.out.println("  Select a checking account:");
        for (int i = 0; i < active.size(); i++) {
            System.out.printf("  [%d] %s  ($%.2f)%n", i + 1, active.get(i).accountID, active.get(i).balance);
        }
        System.out.print("  Enter number: ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice >= 1 && choice <= active.size()) return active.get(choice - 1);
        } catch (NumberFormatException e) { /* fall through */ }
        System.out.println("  Invalid choice.");
        return null;
    }
}