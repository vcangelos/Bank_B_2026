import java.util.*;

public class ATM {

    // foreign currency to usd
    public static double convertToUSD(String currency, double amount) {
        switch (currency.toUpperCase()) {
            case "EUR": return amount * 1.08; // Euro → USD
            case "JPY": return amount * 0.0067; // Yen → USD
            case "GBP": return amount * 1.27; // Pound → USD
            case "CAD": return amount * 0.74; // Canadian Dollar → USD
            case "USD": return amount;
            default:
                System.out.println("Unsupported currency. Assuming USD.");
                return amount;
        }
    }

    public static void startATM(Scanner scanner, List<CheckingAccount.User> users, String checkingPath, String savingsPath, String customerInfoPath) throws Exception {

        System.out.println("\n===== ATM MACHINE =====");

        while (true) {
            System.out.print("\nEnter your User ID (or 'exit'): ");
            String userID = scanner.next();

            if (userID.equalsIgnoreCase("exit")) break;

            CheckingAccount.User user = CheckingAccount.findUser(users, userID);

            if (user == null) {
                System.out.println("Invalid User ID.");
                continue;
            }

            String selectedAccount = CheckingAccount.handleUserEntry(scanner, user, users, checkingPath, customerInfoPath);

            if (selectedAccount == null) continue;

            boolean session = true;

            while (session) {
                System.out.println("\n--- ATM Menu ---");
                System.out.println("1. Check Balance");
                System.out.println("2. Deposit (USD only)");
                System.out.println("3. Withdraw (Supports Foreign Currency)");
                System.out.println("4. Transfer to Savings");
                System.out.println("5. Exit");

                System.out.print("Choose: ");
                int choice;

                try {
                    choice = scanner.nextInt();
                } catch (Exception e) {
                    scanner.next();
                    System.out.println("Invalid input.");
                    continue;
                }

                switch (choice) {

                    case 1:
                        user.printAccounts();
                        break;

                    case 2:
                        System.out.print("Enter deposit amount (USD): ");
                        double dep = scanner.nextDouble();

                        if (dep <= 0) {
                            System.out.println("Invalid amount.");
                        } else {
                            user.deposit(selectedAccount, dep);
                            CheckingAccount.writeCSV(checkingPath, users);
                        }
                        break;

                    case 3:
                        System.out.print("Enter currency (USD, EUR, JPY, GBP, CAD): ");
                        String currency = scanner.next();

                        System.out.print("Enter amount: ");
                        double amount = scanner.nextDouble();

                        if (amount <= 0) {
                            System.out.println("Invalid amount.");
                            break;
                        }

                        // convert to USD
                        double usdAmount = convertToUSD(currency, amount);

                        System.out.printf("Converted to USD: $%.2f%n", usdAmount);

                        // withdraw USD
                        user.withdraw(selectedAccount, usdAmount);
                        CheckingAccount.writeCSV(checkingPath, users);
                        break;

                    case 4:
                        if (user.savingsAccount == null) {
                            System.out.println("No savings account linked.");
                        } else {
                            System.out.print("Enter amount (USD): ");
                            double amt = scanner.nextDouble();

                            if (amt <= 0) {
                                System.out.println("Invalid amount.");
                            } else {
                                user.transferToSavings(selectedAccount, amt);
                                CheckingAccount.writeCSV(checkingPath, users);
                                CheckingAccount.writeSavingsCSV(savingsPath, users);
                            }
                        }
                        break;

                    case 5:
                        System.out.println("Thank you for using the ATM.");
                        session = false;
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }
            }
        }
    }
}