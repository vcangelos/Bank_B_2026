import java.io.*;
import java.util.*;

// have to do 1 case where checking accounts already exist + 1 case where creating new checking account
public class BankingCSV {

    // Inner class representing a single checking account
    static class Account {
        String accountID;
        double balance;

        Account(String accountID, double balance) {
            this.accountID = accountID;
            this.balance   = balance;
        }
    }

    // Inner class representing a single savings account
    static class SavingsAccount {
        String accountID;
        String userID;   // links back to the owning User
        double balance;

        SavingsAccount(String accountID, String userID, double balance) {
            this.accountID = accountID;
            this.userID    = userID;
            this.balance   = balance;
        }
    }

    // Inner class representing a user with multiple accounts
    static class User {
        String userID;
        String name;
        List<Account> accounts;
        boolean hasOverdraftProtection; // true = pull shortfall from savings; false = charge $35 fee
        SavingsAccount savingsAccount;  // linked savings account used for overdraft coverage (may be null)

        static final double OVERDRAFT_FEE = 35.00;

        User(String userID, String name) {
            this.userID   = userID;
            this.name     = name;
            this.accounts = new ArrayList<>();
            this.hasOverdraftProtection = false;
            this.savingsAccount = null;
        }

        void addAccount(Account account) {
            accounts.add(account);
        }

        double getTotalBalance() {
            double total = 0;
            for (Account acc : accounts) total += acc.balance;
            return total;
        }

        void deposit(String accountID, double amount) {
            for (Account acc : accounts) {
                if (acc.accountID.equals(accountID)) {
                    acc.balance += amount;
                    System.out.printf("Deposited $%.2f to %s. New balance: $%.2f%n", amount, accountID, acc.balance);
                    return;
                }
            }
            System.out.println("Account not found: " + accountID);
        }

        void withdraw(String accountID, double amount) {
            for (Account acc : accounts) {
                if (acc.accountID.equals(accountID)) {
                    if (amount <= acc.balance) {
                        // Normal withdrawal — sufficient funds
                        acc.balance -= amount;
                        System.out.printf("Withdrew $%.2f from %s. New balance: $%.2f%n", amount, accountID, acc.balance);
                    } else {
                        // Insufficient funds — check overdraft options
                        double shortfall = amount - acc.balance;
                        if (hasOverdraftProtection && savingsAccount != null) {
                            // Overdraft protection: cover shortfall from savings
                            if (savingsAccount.balance >= shortfall) {
                                savingsAccount.balance -= shortfall;
                                acc.balance = 0;
                                System.out.printf("Withdrew $%.2f from %s (covered $%.2f shortfall from savings %s).%n",
                                    amount, accountID, shortfall, savingsAccount.accountID);
                                System.out.printf("Checking balance: $0.00 | Savings balance: $%.2f%n", savingsAccount.balance);
                            } else {
                                // Savings also insufficient — deny
                                System.out.printf(
                                    "Overdraft protection insufficient. Shortfall: $%.2f, Savings available: $%.2f. Transaction denied.%n",
                                    shortfall, savingsAccount.balance);
                            }
                        } else {
                            // No overdraft protection — apply $35 fee and allow balance to go negative
                            acc.balance -= (amount + OVERDRAFT_FEE);
                            System.out.printf(
                                "Overdraft! Withdrew $%.2f from %s. $%.2f fee applied. New balance: $%.2f%n",
                                amount, accountID, OVERDRAFT_FEE, acc.balance);
                        }
                    }
                    return;
                }
            }
            System.out.println("Account not found: " + accountID);
        }

        // Transfer between two checking accounts (same user)
        void transfer(String fromAccountID, String toAccountID, double amount) {
            Account from = null, to = null;
            for (Account acc : accounts) {
                if (acc.accountID.equals(fromAccountID)) from = acc;
                if (acc.accountID.equals(toAccountID))   to   = acc;
            }
            if (from == null) { System.out.println("Source account not found: " + fromAccountID); return; }
            if (to   == null) { System.out.println("Destination account not found: " + toAccountID); return; }
            if (amount > from.balance) { System.out.println("Insufficient funds in " + fromAccountID); return; }
            from.balance -= amount;
            to.balance   += amount;
            System.out.printf("Transferred $%.2f from checking %s to checking %s.%n", amount, fromAccountID, toAccountID);
        }

        // Transfer from a checking account to the linked savings account
        void transferToSavings(String fromCheckingID, double amount) {
            Account from = null;
            for (Account acc : accounts) {
                if (acc.accountID.equals(fromCheckingID)) { from = acc; break; }
            }
            if (from == null)          { System.out.println("Checking account not found: " + fromCheckingID); return; }
            if (savingsAccount == null) { System.out.println("No savings account linked to your profile."); return; }
            if (amount > from.balance) { System.out.println("Insufficient funds in " + fromCheckingID); return; }
            from.balance           -= amount;
            savingsAccount.balance += amount;
            System.out.printf("Transferred $%.2f from checking %s to savings %s.%n",
                amount, fromCheckingID, savingsAccount.accountID);
            System.out.printf("Checking balance: $%.2f | Savings balance: $%.2f%n",
                from.balance, savingsAccount.balance);
        }


        void printAccounts() {
            System.out.println("--- Checking Accounts ---");
            System.out.printf("%-12s %s%n", "Account ID", "Balance");
            System.out.println("-".repeat(25));
            for (Account acc : accounts) {
                System.out.printf("%-12s $%.2f%n", acc.accountID, acc.balance);
            }
            System.out.printf("%-12s $%.2f%n", "TOTAL", getTotalBalance());
            if (savingsAccount != null) {
                System.out.println("\n--- Savings Account ---");
                System.out.printf("%-12s $%.2f%n", savingsAccount.accountID, savingsAccount.balance);
            }
            System.out.println("\nOverdraft Protection: " + (hasOverdraftProtection ? "Enabled" : "Disabled"));
            if (hasOverdraftProtection && savingsAccount != null) {
                System.out.println("Linked Savings Account: " + savingsAccount.accountID);
            }
        }
    }

    // --- ID Generation ---

    public static Set<String> getAllIDs(List<User> users) {
        Set<String> existing = new HashSet<>();
        for (User user : users) {
            for (Account acc : user.accounts) {
                existing.add(acc.accountID);
            }
        }
        return existing;
    }

    public static String generateID(List<User> users) {
        Set<String> existing = getAllIDs(users);
        Random rand = new Random();
        String newID = "";
        while (newID.isEmpty() || existing.contains(newID)) {
            long number = 400000000000L + (long)(rand.nextDouble() * 100000000000L);
            newID = String.valueOf(number);
        }
        return newID;
    }

    // --- CSV Methods ---

    static String[] headers = {"User ID", "Full Name", "Account ID", "Balance", "Has Savings Account"};

    public static void writeCSV(String filepath, List<User> users) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filepath))) {
            pw.println(String.join(",", headers));
            for (User user : users) {
                for (Account acc : user.accounts) {
                    pw.printf("%s,%s,%s,%.2f,%b%n",
                        user.userID, user.name, acc.accountID, acc.balance, user.hasOverdraftProtection);
                }
            }
        }
        System.out.println("CSV written to: " + filepath);
    }

    public static List<User> readCSV(String filepath) throws IOException {
        Map<String, User> userMap = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String userID    = parts[0];
                String name      = parts[1];
                String accountID = parts[2];
                double balance   = Double.parseDouble(parts[3]);
                boolean hasOverdraft = parts.length > 4 && Boolean.parseBoolean(parts[4]);

                userMap.putIfAbsent(userID, new User(userID, name));
                User user = userMap.get(userID);
                user.hasOverdraftProtection = hasOverdraft;
                user.addAccount(new Account(accountID, balance));
            }
        }
        return new ArrayList<>(userMap.values());
    }

    public static User findUser(List<User> users, String userID) {
        for (User u : users) {
            if (u.userID.equals(userID)) return u;
        }
        return null;
    }

    // --- Savings CSV Methods ---

    static String[] savingsHeaders = {"User ID", "Full Name", "Savings Account ID", "Balance"};

    public static void writeSavingsCSV(String filepath, List<User> users) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filepath))) {
            pw.println(String.join(",", savingsHeaders));
            for (User user : users) {
                if (user.savingsAccount != null) {
                    pw.printf("%s,%s,%s,%.2f%n",
                        user.userID, user.name,
                        user.savingsAccount.accountID,
                        user.savingsAccount.balance);
                }
            }
        }
        System.out.println("Savings CSV written to: " + filepath);
    }

    // Reads savings CSV and links each SavingsAccount to the matching User in the provided list
    public static void readSavingsCSV(String filepath, List<User> users) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts     = line.split(",");
                String userID      = parts[0];
                String accountID   = parts[2];
                double balance     = Double.parseDouble(parts[3]);

                User user = findUser(users, userID);
                if (user != null) {
                    user.savingsAccount = new SavingsAccount(accountID, userID, balance);
                }
            }
        }
    }

    // --- Main ---

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // --- Set up users and accounts ---
        List<User> users = new ArrayList<>();


    
        // --- Menu ---
        System.out.println("\nWelcome " + user.name + "!");
        System.out.println("Menu Options");
        System.out.println("1. Deposit");
        System.out.println("2. Withdraw");
        System.out.println("3. Transfer between accounts");
        System.out.println("4. View transaction history");
        System.out.println("5. Show balances");
        System.out.println("6. Exit");

        boolean on = true;

        while (on) {
            System.out.print("\nPlease enter your selection: ");
            int select = scanner.nextInt();

            // Deposit
            if (select == 1) {
                System.out.print("Please enter the amount you want to deposit: ");
                double deposit = scanner.nextDouble();
                if (deposit <= 0) {
                    System.out.println("Please enter an amount greater than 0.");
                } else {
                    user.deposit(selectedAccount, deposit);
                    System.out.println("$" + deposit + " was successfully deposited.");
                    writeCSV(checkingPath, loadedUsers);
                }

            // Withdraw
            } else if (select == 2) {
                System.out.print("Please enter the amount you want to withdraw: ");
                double withdraw = scanner.nextDouble();
                if (withdraw <= 0) {
                    System.out.println("Amount must be > 0");
                } else {
                    user.withdraw(selectedAccount, withdraw);
                    writeCSV(checkingPath, loadedUsers);
                }

            // Transfer between accounts
            } else if (select == 3) {
                System.out.println("Transfer type:");
                System.out.println("  1. Checking \u2192 Checking");
                System.out.println("  2. Checking \u2192 Savings");
                System.out.print("Enter transfer type: ");
                int transferType = scanner.nextInt();

                if (transferType == 1) {
                    // Checking → Checking
                    if (user.accounts.size() < 2) {
                        System.out.println("You need at least 2 checking accounts to transfer between them.");
                    } else {
                        System.out.println("Your checking accounts:");
                        for (int i = 0; i < user.accounts.size(); i++) {
                            System.out.printf("  %d. %s ($%.2f)%n", i + 1,
                                user.accounts.get(i).accountID,
                                user.accounts.get(i).balance);
                        }
                        System.out.print("Enter source account ID: ");
                        String fromID = scanner.next();
                        System.out.print("Enter destination account ID: ");
                        String toID = scanner.next();
                        System.out.print("Enter amount to transfer: ");
                        double transferAmt = scanner.nextDouble();
                        if (transferAmt <= 0) {
                            System.out.println("Amount must be > 0");
                        } else {
                            user.transfer(fromID, toID, transferAmt);
                            writeCSV(checkingPath, loadedUsers);
                        }
                    }

                } else if (transferType == 2) {
                    // Checking → Savings
                    if (user.savingsAccount == null) {
                        System.out.println("No savings account linked to your profile.");
                    } else {
                        System.out.println("Your checking accounts:");
                        for (int i = 0; i < user.accounts.size(); i++) {
                            System.out.printf("  %d. %s ($%.2f)%n", i + 1,
                                user.accounts.get(i).accountID,
                                user.accounts.get(i).balance);
                        }
                        System.out.printf("Your savings account: %s ($%.2f)%n",
                            user.savingsAccount.accountID, user.savingsAccount.balance);
                        System.out.print("Enter source checking account ID: ");
                        String fromID = scanner.next();
                        System.out.print("Enter amount to transfer: ");
                        double transferAmt = scanner.nextDouble();
                        if (transferAmt <= 0) {
                            System.out.println("Amount must be > 0");
                        } else {
                            user.transferToSavings(fromID, transferAmt);
                            writeCSV(checkingPath, loadedUsers);
                            writeSavingsCSV(savingsPath, loadedUsers);
                        }
                    }

                } else {
                    System.out.println("Invalid transfer type. Please enter 1 or 2.");
                }

            // View transaction history (placeholder)
            } else if (select == 4) {
                System.out.println("Transaction history is not yet implemented.");

            // Show balances
            } else if (select == 5) {
                System.out.println("\n--- Your Accounts ---");
                user.printAccounts();

            // Exit
            } else if (select == 6) {
                System.out.println("Goodbye!");
                on = false;

            } else {
                System.out.println("Invalid selection. Please choose 1-6.");
            }
        }

        // Save final state to both CSVs
        writeCSV(checkingPath, loadedUsers);
        writeSavingsCSV(savingsPath, loadedUsers);
        scanner.close();
    }
}
