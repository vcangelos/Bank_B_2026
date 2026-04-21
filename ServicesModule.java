import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * ServicesModule is the single entry point for Services like Custodial Accounts,
 * Debit Cards, and Currency Conversion.
 */
public class ServicesModule {

    public static void launch(Scanner sc, User appUser, List<CheckingAccount.CheckingUser> checkingUsers) throws IOException {

        DebitCard.setBankingUsers(checkingUsers);

        boolean running = true;
        while (running) {
            System.out.println("\n────────────────────────────────────────");
            System.out.println("BANK  |  Services");
            System.out.println("────────────────────────────────────────");
            System.out.println("  [1] Custodial Accounts");
            System.out.println("  [2] Debit Card");
            System.out.println("  [3] Currency Converter");
            System.out.println("  [0] Back to Dashboard");
            System.out.println("────────────────────────────────────────");
            System.out.print("  Select: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> custodialAccountsMenu(sc, appUser);
                case "2" -> debitCardMenu(sc, appUser, checkingUsers);
                case "3" -> currencyConverterMenu(sc);
                case "0" -> running = false;
                default  -> System.out.println("  Invalid option.");
            }
        }
    }

    private static void custodialAccountsMenu(Scanner sc, User appUser) {
        boolean running = true;
        CustodialReader reader = new CustodialReader();
        CustodialWriter writer = new CustodialWriter();

        while (running) {
            System.out.println("\n────────────────────────────────────────");
            System.out.println("BANK  |  Custodial Accounts");
            System.out.println("────────────────────────────────────────");
            System.out.println("  [1] Create New Custodial Account");
            System.out.println("  [2] View All Accounts");
            System.out.println("  [3] Make a Deposit");
            System.out.println("  [4] Make a Withdrawal");
            System.out.println("  [5] Check Account Info & Update Time");
            System.out.println("  [0] Back to Services");
            System.out.println("────────────────────────────────────────");
            System.out.print("  Select: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    try {
                        new Custodial(); // Interactive constructor
                    } catch (IllegalArgumentException e) {
                        System.out.println("  Error: " + e.getMessage());
                    }
                }
                case "2" -> {
                    ArrayList<Custodial> accounts = reader.readCSV();
                    if (accounts.isEmpty()) {
                        System.out.println("  No custodial accounts found.");
                    }
                }
                case "3" -> {
                    ArrayList<Custodial> accounts = reader.readCSV();
                    if (accounts.isEmpty()) {
                        System.out.println("  No accounts available.");
                        break;
                    }
                    System.out.print("  Enter account number: ");
                    String accNum = sc.nextLine().trim();
                    Custodial account = accounts.stream()
                            .filter(a -> a.getAccountNumber().equals(accNum))
                            .findFirst().orElse(null);
                    if (account != null) {
                        System.out.print("  Enter deposit amount: $");
                        try {
                            double amount = Double.parseDouble(sc.nextLine().trim());
                            account.custodialDeposit(amount);
                            writer.saveData(accounts);
                        } catch (NumberFormatException e) {
                            System.out.println("  Invalid amount.");
                        }
                    } else {
                        System.out.println("  Account not found.");
                    }
                }
                case "4" -> {
                    ArrayList<Custodial> accounts = reader.readCSV();
                    if (accounts.isEmpty()) {
                        System.out.println("  No accounts available.");
                        break;
                    }
                    System.out.print("  Enter account number: ");
                    String accNum = sc.nextLine().trim();
                    Custodial account = accounts.stream()
                            .filter(a -> a.getAccountNumber().equals(accNum))
                            .findFirst().orElse(null);
                    if (account != null) {
                        System.out.print("  Enter withdrawal amount: $");
                        try {
                            double amount = Double.parseDouble(sc.nextLine().trim());
                            account.custodialWithdraw(amount);
                            writer.saveData(accounts);
                        } catch (NumberFormatException e) {
                            System.out.println("  Invalid amount.");
                        }
                    } else {
                        System.out.println("  Account not found.");
                    }
                }
                case "5" -> {
                    ArrayList<Custodial> accounts = reader.readCSV();
                    if (accounts.isEmpty()) {
                        System.out.println("  No accounts available.");
                        break;
                    }
                    System.out.print("  Enter account number: ");
                    String accNum = sc.nextLine().trim();
                    Custodial account = accounts.stream()
                            .filter(a -> a.getAccountNumber().equals(accNum))
                            .findFirst().orElse(null);
                    if (account != null) {
                        account.displayAccountInfo();
                        account.updateTime();
                        writer.saveData(accounts);
                    } else {
                        System.out.println("  Account not found.");
                    }
                }
                case "0" -> running = false;
                default  -> System.out.println("  Invalid option.");
            }
        }
    }

    private static void debitCardMenu(Scanner sc, User appUser, List<CheckingAccount.CheckingUser> checkingUsers) throws IOException {
        DebitCard.setBankingUsers(checkingUsers);

        DebitCard currentCard = DebitCard.loadCard(appUser.customerID);

        boolean running = true;
        while (running) {
            System.out.println("\n────────────────────────────────────────");
            System.out.println("BANK  |  Debit Card Services");
            System.out.println("────────────────────────────────────────");
            System.out.println("  [1] Issue a New Debit Card");
            System.out.println("  [2] Check Balance");
            System.out.println("  [3] Withdraw (Point of Sale)");
            System.out.println("  [4] ATM Withdrawal");
            System.out.println("  [5] Deposit");
            System.out.println("  [6] Foreign Transaction");
            System.out.println("  [7] View Fee Schedule");
            System.out.println("  [8] Replace Card");
            System.out.println("  [9] Close Card");
            System.out.println("  [0] Back to Services");
            System.out.println("────────────────────────────────────────");
            System.out.print("  Select: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    currentCard = DebitCard.issueCard(sc, appUser.customerID);
                    if (currentCard != null) {
                        System.out.println("  Card successfully issued!");
                    }
                }

                case "2" -> {
                    if (currentCard == null) {
                        System.out.println("  No active debit card.");
                        break;
                    }

                    System.out.print("  Enter PIN: ");
                    String pin = sc.nextLine().trim();

                    if (currentCard.verifyPin(pin)) {
                        currentCard.checkBalance();
                    } else {
                        System.out.println("  Invalid PIN.");
                    }
                }

                case "3" -> {
                    if (currentCard == null) {
                        System.out.println("  No active debit card.");
                        break;
                    }

                    System.out.print("  Enter PIN: ");
                    String pin = sc.nextLine().trim();

                    if (!currentCard.verifyPin(pin)) {
                        System.out.println("  Invalid PIN.");
                        break;
                    }

                    System.out.print("  Enter withdrawal amount: $");
                    double amount = Double.parseDouble(sc.nextLine().trim());

                    if (currentCard.withdraw(amount)) {
                        System.out.println("  Withdrawal successful.");
                    } else {
                        System.out.println("  Withdrawal failed.");
                    }
                }

                case "4" -> {
                    if (currentCard == null) {
                        System.out.println("  No active debit card.");
                        break;
                    }

                    System.out.print("  Enter PIN: ");
                    String pin = sc.nextLine().trim();

                    if (!currentCard.verifyPin(pin)) {
                        System.out.println("  Invalid PIN.");
                        break;
                    }

                    System.out.print("  Enter amount: $");
                    double amount = Double.parseDouble(sc.nextLine().trim());

                    System.out.print("  Own bank ATM? (yes/no): ");
                    boolean ownATM = sc.nextLine().trim().equalsIgnoreCase("yes");

                    if (currentCard.withdrawFromATM(amount, ownATM)) {
                        System.out.println("  ATM withdrawal successful.");
                    } else {
                        System.out.println("  Withdrawal failed.");
                    }
                }

                case "5" -> {
                    if (currentCard == null) {
                        System.out.println("  No active debit card.");
                        break;
                    }

                    System.out.print("  Enter amount to deposit: $");
                    double amount = Double.parseDouble(sc.nextLine().trim());

                    if (currentCard.deposit(amount)) {
                        System.out.println("  Deposit successful.");
                    } else {
                        System.out.println("  Deposit failed.");
                    }
                }

                case "6" -> {
                    if (currentCard == null) {
                        System.out.println("  No active debit card.");
                        break;
                    }

                    System.out.print("  Enter amount: $");
                    double amount = Double.parseDouble(sc.nextLine().trim());

                    System.out.print("  Enter currency: ");
                    String currency = sc.nextLine().trim();

                    if (currentCard.foreignTransaction(amount, currency)) {
                        System.out.println("  Transaction successful.");
                    } else {
                        System.out.println("  Transaction failed.");
                    }
                }

                case "7" -> {
                    if (currentCard != null) {
                        currentCard.displayFeeSchedule();
                    } else {
                        System.out.println("  No active card.");
                    }
                }

                case "8" -> {
                    if (currentCard == null) {
                        System.out.println("  No active card.");
                        break;
                    }

                    currentCard = currentCard.replaceCard(sc, "replacement");
                }

                case "9" -> {
                    if (currentCard != null) {
                        currentCard.closeCard();
                        currentCard = null;
                    }
                }

                case "0" -> running = false;

                default -> System.out.println("  Invalid option.");
            }
        }
    }
    private static void currencyConverterMenu(Scanner sc) {
        System.out.println("\n────────────────────────────────────────");
        System.out.println("BANK  |  Currency Converter");
        System.out.println("  Rates: frankfurter.dev");
        System.out.println("────────────────────────────────────────");
        System.out.println("  1.  USD to EUR    2.  EUR to USD");
        System.out.println("  3.  USD to GBP    4.  GBP to USD");
        System.out.println("  5.  EUR to GBP    6.  GBP to EUR");
        System.out.println("  7.  USD to CHF    8.  CHF to USD");
        System.out.println("  9.  EUR to CHF    10. CHF to EUR");
        System.out.println("  11. GBP to CHF    12. CHF to GBP");
        System.out.println("────────────────────────────────────────");
        System.out.print("  Enter your choice (1-12): ");

        int choice;
        try { choice = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("  Invalid choice."); return; }

        System.out.print("  Enter amount: $");
        double amount;
        try { amount = Double.parseDouble(sc.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("  Invalid amount."); return; }

        // Map choice to currency pair
        String[][] pairs = {
                {"USD","EUR"}, {"EUR","USD"}, {"USD","GBP"}, {"GBP","USD"},
                {"EUR","GBP"}, {"GBP","EUR"}, {"USD","CHF"}, {"CHF","USD"},
                {"EUR","CHF"}, {"CHF","EUR"}, {"GBP","CHF"}, {"CHF","GBP"}
        };
        if (choice < 1 || choice > 12) { System.out.println("  Invalid choice."); return; }

        String from = pairs[choice - 1][0];
        String to   = pairs[choice - 1][1];
        try {
            double rate   = CurrencyConverter.getLiveRate(from, to);
            double result = Math.round(amount * rate * 100.0) / 100.0;
            System.out.printf("  %.2f %s = %.2f %s  (rate: %.4f)%n", amount, from, result, to, rate);
        } catch (Exception e) {
            System.out.println("  Error fetching rate: " + e.getMessage());
            System.out.println("  Check your internet connection and try again.");
        }
    }
}