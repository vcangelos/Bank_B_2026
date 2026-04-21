import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * InvestmentsModule is the single entry point called by menu.java for the Investments dashboard.
 * It loads checking/savings once and routes to each investment sub-module.
 *
 * Sub-modules:
 *   [1] Money Market       — moneyMarket.java        (premium savings, $2,500 min, 0.5% interest)
 *   [2] Certificate of Deposit — CertificateOfDeposit.java (lock money for fixed term, 3-5.5% APY)
 *   [3] Treasury Bonds     — TreasuryBondSystem.java  (buy bonds, earn semi-annual interest)
 *   [4] Annuities          — AnnuityBanking.java      (indexed or variable, 5-7 year term)
 */
public class InvestmentsModule {

    public static void launch(Scanner sc, User appUser) throws IOException {

        // Load checking accounts once — shared across all sub-modules
        List<CheckingAccount.Account> checkingAccounts = new ArrayList<>();
        List<CheckingAccount.CheckingUser> checkingUsersRef = new ArrayList<>();
        try {
            checkingUsersRef = CheckingAccount.readCSV("checking_accounts.csv");
            CheckingAccount.CheckingUser cu = CheckingAccount.findUser(checkingUsersRef, appUser.customerID);
            if (cu != null) checkingAccounts = cu.accounts;
        } catch (IOException e) {
            System.out.println("  Note: Could not load checking accounts.");
        }

        // Load savings account
        SavingsAccount savingsAcc = null;
        try {
            savingsAcc = SavingsAccount.OpenSavingsAccount(appUser.customerID);
        } catch (IOException e) {
            System.out.println("  Note: Could not load savings account.");
        }

        // Load CDs into memory on entry
        CertificateOfDeposit.loadActiveCDs();

        // Load annuities on entry (auto-matures any that have come due)
        try {
            AnnuityBanking.loadAnnuities();
            AnnuityBanking.matureAnnuities();
        } catch (IOException e) {
            System.out.println("  Note: Could not load annuities.");
        }

        final List<CheckingAccount.CheckingUser> usersRef  = checkingUsersRef;
        final List<CheckingAccount.Account>      checking  = checkingAccounts;
        final SavingsAccount                     savings   = savingsAcc;

        boolean running = true;
        while (running) {
            System.out.println("\n────────────────────────────────────────");
            System.out.println("BANK  |  Investments");
            System.out.println("────────────────────────────────────────");
            System.out.println("  [1] Money Market");
            System.out.println("      Premium account, $2,500 min, 0.5% monthly interest");
            System.out.println("  [2] Certificate of Deposit (CD)");
            System.out.println("      Lock funds for 6–60 months, earn 3–5.5% APY");
            System.out.println("  [3] Treasury Bonds");
            System.out.println("      Buy bonds, earn semi-annual interest payments");
            System.out.println("  [4] Annuities");
            System.out.println("      Indexed (0–8%, 5yr) or Variable (-10–+10%, 7yr)");
            System.out.println("  [0] Back to Dashboard");
            System.out.println("────────────────────────────────────────");
            System.out.print("  Select: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> launchMoneyMarket(sc, appUser, checking, usersRef);
                case "2" -> launchCD(sc, appUser, checking, savings, usersRef);
                case "3" -> launchBonds(sc, appUser, checking, savings, usersRef);
                case "4" -> launchAnnuities(sc, appUser, checking, savings, usersRef);
                case "0" -> running = false;
                default  -> System.out.println("  Invalid option.");
            }
        }
    }

    // ── Money Market ──────────────────────────────────────────────────────
    private static void launchMoneyMarket(Scanner sc, User appUser,
                                          List<CheckingAccount.Account> checkingAccounts,
                                          List<CheckingAccount.CheckingUser> checkingUsersRef) throws IOException {
        // moneyMarket.launch() is already implemented in moneyMarket.java
        moneyMarket.launch(sc, appUser);
    }

    // ── Certificate of Deposit ────────────────────────────────────────────
    private static void launchCD(Scanner sc, User appUser,
                                 List<CheckingAccount.Account> checkingAccounts,
                                 SavingsAccount savingsAcc,
                                 List<CheckingAccount.CheckingUser> checkingUsersRef) {

        if (checkingAccounts.isEmpty()) {
            System.out.println("  You need a checking account to open a CD.");
            return;
        }
        if (savingsAcc == null) {
            System.out.println("  You need a savings account to receive CD funds at maturity.");
            return;
        }

        // Wire CertificateOfDeposit to CheckingAccount.CheckingUser (replaces BankingCSV dependency)
        CheckingAccount.CheckingUser checkUser = CheckingAccount.findUser(checkingUsersRef, appUser.customerID);
        if (checkUser == null) {
            System.out.println("  Could not find your checking account data.");
            return;
        }

        // Set banking users so CertificateOfDeposit can find the checking account
        // We wrap CheckingUser into the interface CertificateOfDeposit expects
        CertificateOfDeposit.setCheckingUser(checkUser);

        boolean running = true;
        while (running) {
            System.out.println("\n────────────────────────────────────────");
            System.out.println("BANK  |  Certificate of Deposit");
            System.out.println("────────────────────────────────────────");
            System.out.println("  [1] Open a new CD");
            System.out.println("  [2] View / Manage existing CDs");
            System.out.println("  [0] Back");
            System.out.println("────────────────────────────────────────");
            System.out.print("  Select: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> CertificateOfDeposit.welcomeScreen(sc, appUser.customerID);
                case "2" -> CertificateOfDeposit.manageCD(sc, appUser.customerID);
                case "0" -> running = false;
                default  -> System.out.println("  Invalid option.");
            }
        }

        // Sync CD balance back to appUser
        appUser.cdBalance = CertificateOfDeposit.getActiveCDBalance(appUser.customerID);
    }

    // ── Treasury Bonds ────────────────────────────────────────────────────
    private static void launchBonds(Scanner sc, User appUser,
                                    List<CheckingAccount.Account> checkingAccounts,
                                    SavingsAccount savingsAcc,
                                    List<CheckingAccount.CheckingUser> checkingUsersRef) throws IOException {

        if (checkingAccounts.isEmpty()) {
            System.out.println("  You need a checking account to buy bonds.");
            return;
        }

        CheckingAccount.CheckingUser checkUser = CheckingAccount.findUser(checkingUsersRef, appUser.customerID);
        if (checkUser == null) { System.out.println("  Could not load checking data."); return; }

        // Pick the checking account to use for this bond session
        CheckingAccount.Account selectedAcc = pickAccount(sc, checkingAccounts);
        if (selectedAcc == null) return;

        // Build TreasuryBondSystem.User from our CheckingUser
        TreasuryBondSystem.User bondUser = TreasuryBondSystem.createBondUser(
                wrapCheckingUser(checkUser, savingsAcc), selectedAcc.accountID);
        if (bondUser == null) { System.out.println("  Could not start bond session."); return; }

        TreasuryBondSystem.loadBonds(bondUser, "bonds.csv");

        boolean running = true;
        while (running) {
            System.out.println("\n────────────────────────────────────────");
            System.out.println("BANK  |  Treasury Bonds");
            System.out.println("────────────────────────────────────────");
            System.out.printf ("  Checking (%s): $%.2f%n", bondUser.checking.accountID, bondUser.checking.balance);
            if (bondUser.savings != null)
                System.out.printf("  Savings (%s): $%.2f%n", bondUser.savings.accountID, bondUser.savings.balance);
            System.out.println("  [1] Buy Bond (from Checking)");
            System.out.println("  [2] Buy Bond (from Savings)");
            System.out.println("  [3] Pay Interest on all Bonds");
            System.out.println("  [4] Redeem all matured Bonds");
            System.out.println("  [5] View my Bonds");
            System.out.println("  [0] Back");
            System.out.println("────────────────────────────────────────");
            System.out.print("  Select: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    try {
                        System.out.print("  Bond amount: $");
                        double amt  = Double.parseDouble(sc.nextLine().trim());
                        System.out.print("  Rate (e.g. 0.05 for 5%): ");
                        double rate = Double.parseDouble(sc.nextLine().trim());
                        System.out.print("  Years: ");
                        int years   = Integer.parseInt(sc.nextLine().trim());
                        TreasuryBondSystem.buyBond(bondUser, true, amt, rate, years);
                        TreasuryBondSystem.saveBonds(bondUser, "bonds.csv");
                        CheckingAccount.writeCSV("checking_accounts.csv", checkingUsersRef);
                        appUser.checkingAccount = selectedAcc.balance;
                    } catch (NumberFormatException e) { System.out.println("  Invalid input."); }
                }
                case "2" -> {
                    if (savingsAcc == null) { System.out.println("  No savings account linked."); break; }
                    try {
                        System.out.print("  Bond amount: $");
                        double amt  = Double.parseDouble(sc.nextLine().trim());
                        System.out.print("  Rate (e.g. 0.05 for 5%): ");
                        double rate = Double.parseDouble(sc.nextLine().trim());
                        System.out.print("  Years: ");
                        int years   = Integer.parseInt(sc.nextLine().trim());
                        TreasuryBondSystem.buyBond(bondUser, false, amt, rate, years);
                        TreasuryBondSystem.saveBonds(bondUser, "bonds.csv");
                        appUser.savingsAccount = savingsAcc.getSavings();
                    } catch (NumberFormatException e) { System.out.println("  Invalid input."); }
                }
                case "3" -> {
                    TreasuryBondSystem.payInterest(bondUser);
                    TreasuryBondSystem.saveBonds(bondUser, "bonds.csv");
                    CheckingAccount.writeCSV("checking_accounts.csv", checkingUsersRef);
                    appUser.checkingAccount = selectedAcc.balance;
                }
                case "4" -> {
                    TreasuryBondSystem.redeem(bondUser);
                    TreasuryBondSystem.saveBonds(bondUser, "bonds.csv");
                    CheckingAccount.writeCSV("checking_accounts.csv", checkingUsersRef);
                    appUser.checkingAccount = selectedAcc.balance;
                }
                case "5" -> TreasuryBondSystem.showBonds(bondUser);
                case "0" -> running = false;
                default  -> System.out.println("  Invalid option.");
            }
        }
    }

    // ── Annuities ─────────────────────────────────────────────────────────
    private static void launchAnnuities(Scanner sc, User appUser,
                                        List<CheckingAccount.Account> checkingAccounts,
                                        SavingsAccount savingsAcc,
                                        List<CheckingAccount.CheckingUser> checkingUsersRef) throws IOException {

        // Build an AnnuityBanking.User from our BankApp user
        AnnuityBanking.User annUser = buildAnnuityUser(appUser, checkingAccounts, savingsAcc);

        boolean running = true;
        while (running) {
            System.out.println("\n────────────────────────────────────────");
            System.out.println("BANK  |  Annuities");
            System.out.println("────────────────────────────────────────");
            System.out.println("  [1] View my accounts");
            System.out.println("  [2] Buy an Annuity");
            System.out.println("  [3] View my Annuities");
            System.out.println("  [4] Surrender an Annuity");
            System.out.println("  [0] Back");
            System.out.println("────────────────────────────────────────");
            System.out.print("  Select: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> annUser.printAccounts();
                case "2" -> {
                    AnnuityBanking.buyAnnuityFlow(sc, annUser);
                    // Sync balances back after purchase
                    syncAnnuityBalances(annUser, appUser, checkingAccounts, savingsAcc);
                    CheckingAccount.writeCSV("checking_accounts.csv", checkingUsersRef);
                    AnnuityBanking.saveMainCSV();
                    AnnuityBanking.saveAnnuitiesCSV();
                }
                case "3" -> AnnuityBanking.viewAnnuities(annUser);
                case "4" -> {
                    AnnuityBanking.surrenderAnnuityFlow(sc, annUser);
                    syncAnnuityBalances(annUser, appUser, checkingAccounts, savingsAcc);
                    CheckingAccount.writeCSV("checking_accounts.csv", checkingUsersRef);
                    AnnuityBanking.saveMainCSV();
                    AnnuityBanking.saveAnnuitiesCSV();
                }
                case "0" -> running = false;
                default  -> System.out.println("  Invalid option.");
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /** Shared account picker used across investment sub-modules. */
    public static CheckingAccount.Account pickAccount(Scanner sc, List<CheckingAccount.Account> accounts) {
        List<CheckingAccount.Account> active = new ArrayList<>();
        for (CheckingAccount.Account a : accounts) {
            if (a.isActive) active.add(a);
        }
        if (active.isEmpty()) { System.out.println("  No active checking accounts."); return null; }
        System.out.println("  Select a checking account:");
        for (int i = 0; i < active.size(); i++)
            System.out.printf("  [%d] %s  ($%.2f)%n", i + 1, active.get(i).accountID, active.get(i).balance);
        System.out.print("  Enter number: ");
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            if (choice >= 1 && choice <= active.size()) return active.get(choice - 1);
        } catch (NumberFormatException e) { /* fall through */ }
        System.out.println("  Invalid choice.");
        return null;
    }

    /**
     * Wraps a CheckingAccount.CheckingUser into a CheckingAccount.User-shaped object
     * that TreasuryBondSystem expects. TreasuryBondSystem.User holds a bankUser
     * (CheckingAccount.User type) — since we renamed it to CheckingUser, we need
     * to pass it through createBondUser which takes CheckingAccount.CheckingUser.
     */
    private static CheckingAccount.CheckingUser wrapCheckingUser(
            CheckingAccount.CheckingUser checkUser, SavingsAccount savingsAcc) {
        // If the user has a savings account, populate the CheckingUser's savingsAccount field
        // so TreasuryBondSystem can debit it for savings-funded bonds.
        if (savingsAcc != null && checkUser.savingsAccount == null) {
            checkUser.savingsAccount = new CheckingAccount.SavingsAccount(
                    savingsAcc.getSavingsID(), checkUser.userID, savingsAcc.getSavings());
        }
        return checkUser;
    }

    /**
     * Builds an AnnuityBanking.User from the logged-in BankApp User.
     * AnnuityBanking uses its own LinkedAccount type — we build those from
     * the checking and savings accounts we already have loaded.
     */
    private static AnnuityBanking.User buildAnnuityUser(User appUser,
                                                        List<CheckingAccount.Account> checkingAccounts, SavingsAccount savingsAcc) {

        AnnuityBanking.User annUser = new AnnuityBanking.User(
                appUser.customerID,
                appUser.firstName + " " + appUser.lastName,
                appUser.password); // PIN = hashed password (already in memory)

        for (CheckingAccount.Account acc : checkingAccounts) {
            if (acc.isActive) {
                annUser.addAccount(new AnnuityBanking.LinkedAccount(
                        acc.accountID, "CHECKING", acc.balance, 0.0, true));
            }
        }

        if (savingsAcc != null) {
            annUser.addAccount(new AnnuityBanking.LinkedAccount(
                    savingsAcc.getSavingsID(), "SAVINGS", savingsAcc.getSavings(), 0.0, true));
        }

        if (appUser.creditCard != null && !appUser.creditCard.isEmpty()) {
            annUser.addAccount(new AnnuityBanking.LinkedAccount(
                    appUser.creditCard, "CREDIT", 0.0, appUser.creditCardLimit, true));
        }

        // Register with AnnuityBanking's global user list so buyAnnuityFlow can find them
        AnnuityBanking.allUsers.removeIf(u -> u.userID.equals(appUser.customerID));
        AnnuityBanking.allUsers.add(annUser);

        return annUser;
    }

    /**
     * After an annuity purchase or surrender, the AnnuityBanking.LinkedAccount balances
     * have been modified. Sync those changes back to the actual CheckingAccount.Account
     * objects and savingsAcc so the changes persist when we writeCSV.
     */
    private static void syncAnnuityBalances(AnnuityBanking.User annUser, User appUser,
                                            List<CheckingAccount.Account> checkingAccounts, SavingsAccount savingsAcc) {

        for (AnnuityBanking.LinkedAccount linked : annUser.accounts) {
            if (linked.type.equals("CHECKING")) {
                for (CheckingAccount.Account acc : checkingAccounts) {
                    if (acc.accountID.equals(linked.accountID)) {
                        acc.balance = linked.balance;
                        appUser.checkingAccount = linked.balance;
                    }
                }
            } else if (linked.type.equals("SAVINGS") && savingsAcc != null) {
                savingsAcc.setSavings(linked.balance);
                appUser.savingsAccount = linked.balance;
            }
        }
    }
}