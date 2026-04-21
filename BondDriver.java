// BondDriver.java
import java.util.*;
import java.io.*;

public class BondDriver {

    private static final String CHECKING_CSV = "checking_accounts.csv";
    private static final String SAVINGS_CSV  = "Savings.csv";
    private static final String BONDS_CSV    = "bonds.csv";
    private static final String CUSTOMERINFO_CSV = "customerInfo.csv";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            List<CheckingAccount.CheckingUser> loadedUsers = CheckingAccount.readCSV(CHECKING_CSV);

            try {
                CheckingAccount.readSavingsCSV(SAVINGS_CSV, loadedUsers);
            } catch (FileNotFoundException e) {
                System.out.println("Savings.csv not found. Users can still use checking for bonds.");
            }

            System.out.println("\n=============================");
            System.out.println("      TREASURY BOND MENU");
            System.out.println("=============================");

            System.out.print("Enter your User ID: ");
            String inputID = sc.next().trim();

            CheckingAccount.CheckingUser bankUser = CheckingAccount.findUser(loadedUsers, inputID);
            if (bankUser == null) {
                System.out.println("User ID not found.");
                sc.close();
                return;
            }

            String selectedCheckingID =
                    CheckingAccount.handleUserEntry(sc, bankUser, loadedUsers, CHECKING_CSV, CUSTOMERINFO_CSV);

            if (selectedCheckingID == null) {
                System.out.println("Unable to open a checking account session.");
                sc.close();
                return;
            }

            TreasuryBondSystem.User bondUser =
                    TreasuryBondSystem.createBondUser(bankUser, selectedCheckingID);

            if (bondUser == null) {
                System.out.println("Unable to start bond session.");
                sc.close();
                return;
            }

            TreasuryBondSystem.loadBonds(bondUser, BONDS_CSV);

            boolean running = true;

            while (running) {
                System.out.println("\n=== TREASURY BOND MENU ===");
                System.out.println("1. Show Balances");
                System.out.println("2. Buy Bond (Checking)");
                System.out.println("3. Buy Bond (Savings)");
                System.out.println("4. Pay Interest");
                System.out.println("5. Redeem Bonds");
                System.out.println("6. View Bonds");
                System.out.println("7. Save Bonds CSV");
                System.out.println("8. Reload Bonds CSV");
                System.out.println("9. Exit");
                System.out.print("Choice: ");

                int choice;
                try {
                    choice = sc.nextInt();
                } catch (InputMismatchException e) {
                    sc.next();
                    System.out.println("Invalid choice.");
                    continue;
                }

                switch (choice) {
                    case 1 -> showBalances(bondUser);

                    case 2 -> {
                        System.out.print("Bond amount: ");
                        double amt = sc.nextDouble();
                        System.out.print("Rate (example 0.05 for 5%): ");
                        double rate = sc.nextDouble();
                        System.out.print("Years: ");
                        int years = sc.nextInt();

                        TreasuryBondSystem.buyBond(bondUser, true, amt, rate, years);
                        persistAll(loadedUsers, bondUser);
                    }

                    case 3 -> {
                        if (bondUser.savings == null) {
                            System.out.println("No savings account linked to this user.");
                            break;
                        }

                        System.out.print("Bond amount: ");
                        double amt = sc.nextDouble();
                        System.out.print("Rate (example 0.05 for 5%): ");
                        double rate = sc.nextDouble();
                        System.out.print("Years: ");
                        int years = sc.nextInt();

                        TreasuryBondSystem.buyBond(bondUser, false, amt, rate, years);
                        persistAll(loadedUsers, bondUser);
                    }

                    case 4 -> {
                        TreasuryBondSystem.payInterest(bondUser);
                        persistAll(loadedUsers, bondUser);
                    }

                    case 5 -> {
                        TreasuryBondSystem.redeem(bondUser);
                        persistAll(loadedUsers, bondUser);
                    }

                    case 6 -> TreasuryBondSystem.showBonds(bondUser);

                    case 7 -> {
                        TreasuryBondSystem.saveBonds(bondUser, BONDS_CSV);
                        System.out.println("Bond CSV saved.");
                    }

                    case 8 -> TreasuryBondSystem.loadBonds(bondUser, BONDS_CSV);

                    case 9 -> {
                        persistAll(loadedUsers, bondUser);
                        running = false;
                    }

                    default -> System.out.println("Invalid option.");
                }
            }

            System.out.println("Treasury bond session closed.");

        } catch (Exception e) {
            System.out.println("Program error: " + e.getMessage());
        } finally {
            sc.close();
        }
    }

    private static void showBalances(TreasuryBondSystem.User user) {
        System.out.printf("Checking (%s): $%.2f%n",
                user.checking.accountID, user.checking.balance);

        if (user.savings != null) {
            System.out.printf("Savings (%s): $%.2f%n",
                    user.savings.accountID, user.savings.balance);
        } else {
            System.out.println("Savings: No linked savings account");
        }
    }

    private static void persistAll(List<CheckingAccount.CheckingUser> loadedUsers,
                                   TreasuryBondSystem.User bondUser) {
        try {
            CheckingAccount.writeCSV(CHECKING_CSV, loadedUsers);
            CheckingAccount.writeSavingsCSV(SAVINGS_CSV, loadedUsers);
            TreasuryBondSystem.saveBonds(bondUser, BONDS_CSV);
        } catch (IOException e) {
            System.out.println("Error saving updated account/bond data: " + e.getMessage());
        }
    }
}