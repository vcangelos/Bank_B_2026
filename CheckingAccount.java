import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.InputMismatchException;
import java.time.temporal.ChronoUnit;
import java.io.*;


public class CheckingAccount {

    static class Account {
        String accountID;
        double balance;
        boolean belowMinBalance;
        boolean isActive;
        LocalDate wentNegativeDate;
        LocalDateTime minBalWarnedAt;

        String[][] transactionHistory = new String[100][4];
        int transactionCount = 0;

        void addTransaction(String type, double amount) {
            if (transactionCount < 100) {
                transactionHistory[transactionCount][0] = type;
                transactionHistory[transactionCount][1] = " " + amount;
                transactionHistory[transactionCount][2] = LocalDate.now().toString();
                transactionHistory[transactionCount][3] = " " + this.balance;
                transactionCount++;
            }
        }

        void printTransactionHistory() {
            System.out.println("Transaction History: ");
            if (transactionCount == 0) { System.out.println("No transactions yet."); return; }
            for (int i = 0; i < transactionCount; i++) {
                System.out.println("Type: "           + transactionHistory[i][0]);
                System.out.println("Amount:$ "        + transactionHistory[i][1]);
                System.out.println("Date: "           + transactionHistory[i][2]);
                System.out.println("Balance After:$ " + transactionHistory[i][3]);
            }
        }

        Account(String accountID, double balance) {
            this.accountID       = accountID;
            this.balance         = balance;
            this.belowMinBalance = balance < MIN_BALANCE;
            this.isActive        = true;
            this.wentNegativeDate = null;
            this.minBalWarnedAt   = null;
        }

        Account(String accountID, double balance, boolean belowMinBalance, boolean isActive,
                LocalDate wentNegativeDate, LocalDateTime minBalWarnedAt) {
            this.accountID        = accountID;
            this.balance          = balance;
            this.belowMinBalance  = belowMinBalance;
            this.isActive         = isActive;
            this.wentNegativeDate = wentNegativeDate;
            this.minBalWarnedAt   = minBalWarnedAt;
        }

        void updateFlags() {
            this.belowMinBalance = this.balance < MIN_BALANCE;
            if (this.balance < 0) {
                if (this.wentNegativeDate == null) {
                    this.wentNegativeDate = LocalDate.now();
                } else {
                    long daysNegative = ChronoUnit.DAYS.between(this.wentNegativeDate, LocalDate.now());
                    if (daysNegative >= 30) this.isActive = false;
                }
            } else {
                this.wentNegativeDate = null;
                if (!this.isActive && this.balance >= 0) this.isActive = true;
            }
            if (this.balance >= MIN_BALANCE && this.minBalWarnedAt != null) {
                System.out.printf("[Min-Balance] Account %s is back above $%.2f. Warning cleared.%n",
                        this.accountID, MIN_BALANCE);
                this.minBalWarnedAt = null;
            }
        }

        boolean checkAndApplyMinBalanceFee() {
            if (this.balance >= MIN_BALANCE) return false;
            if (this.minBalWarnedAt == null) {
                this.minBalWarnedAt = LocalDateTime.now();
                System.out.printf(
                        "[Min-Balance WARNING] Account %s balance ($%.2f) is below the $%.2f minimum.%n" +
                                "  A $%.2f fee will be charged if not restored within 24 hours.%n" +
                                "  Warning issued at: %s%n",
                        this.accountID, this.balance, MIN_BALANCE, MIN_BALANCE_FEE,
                        this.minBalWarnedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                return false;
            }
            long hoursElapsed = Duration.between(this.minBalWarnedAt, LocalDateTime.now()).toHours();
            if (hoursElapsed >= 24) {
                this.balance -= MIN_BALANCE_FEE;
                this.minBalWarnedAt = LocalDateTime.now();
                this.updateFlags();
                System.out.printf(
                        "[Min-Balance FEE] 24-hour grace period expired for account %s.%n" +
                                "  $%.2f fee charged. New balance: $%.2f%n",
                        this.accountID, MIN_BALANCE_FEE, this.balance);
                return true;
            } else {
                long hoursLeft = 24 - hoursElapsed;
                System.out.printf(
                        "[Min-Balance REMINDER] Account %s still below $%.2f (balance: $%.2f).%n" +
                                "  ~%d hour(s) remaining before a $%.2f fee is charged.%n",
                        this.accountID, MIN_BALANCE, this.balance, hoursLeft, MIN_BALANCE_FEE);
                return false;
            }
        }
    }

    static final double MIN_BALANCE     = 100.00;
    static final double MIN_BALANCE_FEE =   5.00;
    static final double OVERDRAFT_FEE   =  35.00;
    static final DateTimeFormatter DT_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    static class SavingsAccount {
        String accountID;
        String userID;
        double balance;

        SavingsAccount(String accountID, String userID, double balance) {
            this.accountID = accountID;
            this.userID    = userID;
            this.balance   = balance;
        }
    }

    // Renamed from User -> CheckingUser to avoid conflict with BankApp's User.java
    static class CheckingUser {
        String userID;
        String name;
        List<Account> accounts;
        boolean hasOverdraftProtection;
        SavingsAccount savingsAccount;

        CheckingUser(String userID, String name) {
            this.userID                 = userID;
            this.name                   = name;
            this.accounts               = new ArrayList<>();
            this.hasOverdraftProtection = false;
            this.savingsAccount         = null;
        }

        void addAccount(Account account) { accounts.add(account); }

        double getTotalBalance() {
            double total = 0;
            for (Account acc : accounts) total += acc.balance;
            return total;
        }

        void applyExpiredMinBalanceFees() {
            for (Account acc : accounts) {
                if (acc.minBalWarnedAt != null && acc.balance < MIN_BALANCE) {
                    long hoursElapsed = Duration.between(acc.minBalWarnedAt, LocalDateTime.now()).toHours();
                    if (hoursElapsed >= 24) {
                        acc.balance -= MIN_BALANCE_FEE;
                        acc.minBalWarnedAt = LocalDateTime.now();
                        acc.updateFlags();
                        System.out.printf(
                                "[Min-Balance FEE on load] Grace period expired for account %s.%n" +
                                        "  $%.2f fee charged. New balance: $%.2f%n",
                                acc.accountID, MIN_BALANCE_FEE, acc.balance);
                    }
                }
            }
        }

        void deposit(String accountID, double amount) {
            for (Account acc : accounts) {
                if (acc.accountID.equals(accountID)) {
                    if (!acc.isActive) { System.out.println("Account " + accountID + " is inactive and cannot accept deposits."); return; }
                    acc.balance += amount;
                    acc.updateFlags();
                    acc.addTransaction("Deposit", amount);
                    System.out.printf("Deposited $%.2f to %s. New balance: $%.2f%n", amount, accountID, acc.balance);
                    acc.checkAndApplyMinBalanceFee();
                    return;
                }
            }
            System.out.println("Account not found: " + accountID);
        }

        void withdraw(String accountID, double amount) {
            for (Account acc : accounts) {
                if (acc.accountID.equals(accountID)) {
                    if (!acc.isActive) { System.out.println("Account " + accountID + " is inactive and cannot process withdrawals."); return; }
                    if (amount <= acc.balance) {
                        acc.balance -= amount;
                        acc.addTransaction("Withdrawal", amount);
                        acc.updateFlags();
                        System.out.printf("Withdrew $%.2f from %s. New balance: $%.2f%n", amount, accountID, acc.balance);
                    } else {
                        double shortfall = amount - acc.balance;
                        if (hasOverdraftProtection && savingsAccount != null) {
                            if (savingsAccount.balance >= shortfall) {
                                savingsAccount.balance -= shortfall;
                                acc.balance = 0;
                                acc.updateFlags();
                                acc.addTransaction("Withdrawal", amount);
                                System.out.printf("Withdrew $%.2f from %s (covered $%.2f shortfall from savings %s).%n",
                                        amount, accountID, shortfall, savingsAccount.accountID);
                                System.out.printf("Checking balance: $0.00 | Savings balance: $%.2f%n", savingsAccount.balance);
                            } else {
                                System.out.printf(
                                        "Overdraft protection insufficient. Shortfall: $%.2f, Savings available: $%.2f. Transaction denied.%n",
                                        shortfall, savingsAccount.balance);
                                return;
                            }
                        } else {
                            acc.balance -= (amount + OVERDRAFT_FEE);
                            acc.addTransaction("Withdrawal", amount);
                            acc.addTransaction("Overdraft Fee", OVERDRAFT_FEE);
                            acc.updateFlags();
                            System.out.printf(
                                    "Overdraft! Withdrew $%.2f from %s. $%.2f fee applied. New balance: $%.2f%n",
                                    amount, accountID, OVERDRAFT_FEE, acc.balance);
                            if (!acc.isActive) {
                                System.out.println("WARNING: Account " + accountID + " has been deactivated due to 30+ days in negative balance.");
                            } else if (acc.wentNegativeDate != null) {
                                long days = ChronoUnit.DAYS.between(acc.wentNegativeDate, LocalDate.now());
                                System.out.printf("WARNING: Account %s has been negative for %d day(s). It will be deactivated after 30 days.%n", accountID, days);
                            }
                        }
                    }
                    acc.checkAndApplyMinBalanceFee();
                    return;
                }
            }
            System.out.println("Account not found: " + accountID);
        }

        void transfer(String fromAccountID, String toAccountID, double amount) {
            Account from = null, to = null;
            for (Account acc : accounts) {
                if (acc.accountID.equals(fromAccountID)) from = acc;
                if (acc.accountID.equals(toAccountID))   to   = acc;
            }
            if (from == null)   { System.out.println("Source account not found: " + fromAccountID); return; }
            if (to   == null)   { System.out.println("Destination account not found: " + toAccountID); return; }
            if (!from.isActive) { System.out.println("Source account " + fromAccountID + " is inactive."); return; }
            if (!to.isActive)   { System.out.println("Destination account " + toAccountID + " is inactive."); return; }
            if (amount > from.balance) { System.out.println("Insufficient funds in " + fromAccountID); return; }
            from.balance -= amount;
            to.balance   += amount;
            from.addTransaction("Transfer Out", amount);
            to.addTransaction("Transfer In", amount);
            from.updateFlags();
            to.updateFlags();
            System.out.printf("Transferred $%.2f from checking %s to checking %s.%n", amount, fromAccountID, toAccountID);
            from.checkAndApplyMinBalanceFee();
        }

        void transferToSavings(String fromCheckingID, double amount) {
            Account from = null;
            for (Account acc : accounts) {
                if (acc.accountID.equals(fromCheckingID)) { from = acc; break; }
            }
            if (from == null)           { System.out.println("Checking account not found: " + fromCheckingID); return; }
            if (!from.isActive)         { System.out.println("Account " + fromCheckingID + " is inactive."); return; }
            if (savingsAccount == null) { System.out.println("No savings account linked to your profile."); return; }
            if (amount > from.balance)  { System.out.println("Insufficient funds in " + fromCheckingID); return; }
            from.balance           -= amount;
            savingsAccount.balance += amount;
            from.addTransaction("Transfer to Savings", amount);
            from.updateFlags();
            System.out.printf("Transferred $%.2f from checking %s to savings %s.%n", amount, fromCheckingID, savingsAccount.accountID);
            System.out.printf("Checking balance: $%.2f | Savings balance: $%.2f%n", from.balance, savingsAccount.balance);
            from.checkAndApplyMinBalanceFee();
        }

        void printAccounts() {
            System.out.println("--- Checking Accounts ---");
            System.out.printf("%-14s %-12s %-14s %-10s %-16s %s%n",
                    "Account ID", "Balance", "Below Min Bal", "Active", "Negative Since", "Min-Bal Warning");
            System.out.println("-".repeat(90));
            for (Account acc : accounts) {
                String negSince  = acc.wentNegativeDate != null ? acc.wentNegativeDate.toString() : "N/A";
                String warnInfo  = "";
                if (acc.minBalWarnedAt != null) {
                    long hoursLeft = 24 - Duration.between(acc.minBalWarnedAt, LocalDateTime.now()).toHours();
                    warnInfo = String.format("~%dh until $%.2f fee", hoursLeft, MIN_BALANCE_FEE);
                }
                System.out.printf("%-14s $%-11.2f %-14s %-10s %-16s %s%n",
                        acc.accountID, acc.balance,
                        acc.belowMinBalance ? "Yes" : "No",
                        acc.isActive        ? "Yes" : "No",
                        negSince, warnInfo);
            }
            System.out.printf("%-14s $%.2f%n", "TOTAL", getTotalBalance());
            if (savingsAccount != null) {
                System.out.println("\n--- Savings Account ---");
                System.out.printf("%-14s $%.2f%n", savingsAccount.accountID, savingsAccount.balance);
            }
            System.out.println("\nOverdraft Protection: " + (hasOverdraftProtection ? "Enabled" : "Disabled"));
            if (hasOverdraftProtection && savingsAccount != null) {
                System.out.println("Linked Savings Account: " + savingsAccount.accountID);
            }
        }
    }

    // --- ID Generation ---

    public static Set<String> getAllIDs(List<CheckingUser> users) {
        Set<String> existing = new HashSet<>();
        for (CheckingUser user : users)
            for (Account acc : user.accounts)
                existing.add(acc.accountID);
        return existing;
    }

    public static String generateID(List<CheckingUser> users) {
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

    static String[] headers = {
            "User ID", "Full Name", "Account ID", "Balance",
            "Has Overdraft Protection", "Below Min Balance", "Is Active",
            "Went Negative Date", "Min Bal Warned At"
    };

    public static void writeCSV(String filepath, List<CheckingUser> users) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filepath))) {
            pw.println(String.join(",", headers));
            for (CheckingUser user : users) {
                if (user.accounts.isEmpty()) {
                    pw.printf("%s,%s,NO_ACCOUNT,0.00,false,false,true,,%n", user.userID, user.name);
                } else {
                    for (Account acc : user.accounts) {
                        String negDate  = acc.wentNegativeDate != null ? acc.wentNegativeDate.toString() : "";
                        String warnedAt = acc.minBalWarnedAt   != null ? acc.minBalWarnedAt.format(DT_FMT) : "";
                        pw.printf("%s,%s,%s,%.2f,%b,%b,%b,%s,%s%n",
                                user.userID, user.name, acc.accountID, acc.balance,
                                user.hasOverdraftProtection,
                                acc.belowMinBalance,
                                acc.isActive,
                                negDate,
                                warnedAt);
                    }
                }
            }
        }
        System.out.println("CSV written to: " + filepath);
    }

    public static List<CheckingUser> readCSV(String filepath) throws IOException {
        Map<String, CheckingUser> userMap = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts       = line.split(",", -1);
                String userID        = parts[0];
                String name          = parts[1];
                String accountID     = parts[2];
                double balance       = Double.parseDouble(parts[3]);
                boolean hasOverdraft = parts.length > 4 && Boolean.parseBoolean(parts[4]);
                boolean belowMin     = parts.length > 5 && Boolean.parseBoolean(parts[5]);
                boolean isActive     = parts.length <= 6 || Boolean.parseBoolean(parts[6]);
                LocalDate negDate    = (parts.length > 7 && !parts[7].isBlank())
                        ? LocalDate.parse(parts[7]) : null;
                LocalDateTime warnedAt = (parts.length > 8 && !parts[8].isBlank())
                        ? LocalDateTime.parse(parts[8], DT_FMT) : null;

                userMap.putIfAbsent(userID, new CheckingUser(userID, name));
                CheckingUser user = userMap.get(userID);
                user.hasOverdraftProtection = hasOverdraft;
                if (!accountID.equals("NO_ACCOUNT")) {
                    user.addAccount(new Account(accountID, balance, belowMin, isActive, negDate, warnedAt));
                }
            }
        }
        return new ArrayList<>(userMap.values());
    }

    public static CheckingUser findUser(List<CheckingUser> users, String userID) {
        for (CheckingUser u : users) if (u.userID.equals(userID)) return u;
        return null;
    }

    // --- Savings CSV Methods ---

    static String[] savingsHeaders = {"userid", "SavingsID", "Savings"};

    public static void writeSavingsCSV(String filepath, List<CheckingUser> users) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filepath))) {
            pw.println(String.join(",", savingsHeaders));
            for (CheckingUser user : users) {
                if (user.savingsAccount != null) {
                    pw.printf("%s,%s,%.1f%n",
                            user.userID,
                            user.savingsAccount.accountID,
                            user.savingsAccount.balance);
                }
            }
        }
        System.out.println("Savings CSV written to: " + filepath);
    }

    public static void readSavingsCSV(String filepath, List<CheckingUser> users) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts   = line.split(",");
                String userID    = parts[0];
                String accountID = parts[1];
                double balance   = Double.parseDouble(parts[2]);
                CheckingUser user = findUser(users, userID);
                if (user != null) user.savingsAccount = new SavingsAccount(accountID, userID, balance);
            }
        }
    }

    // --- Customer Info CSV ---

    public static void updateCustomerInfoCSV(String customerInfoPath, List<CheckingUser> users) {
        File file = new File(customerInfoPath);
        if (!file.exists()) return;

        List<String> lines = new ArrayList<>();
        String header = "";
        int checkingColIndex   = -1;
        int customerIDColIndex = -1;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            header = br.readLine();
            if (header == null) return;

            String[] cols = header.split(",", -1);
            for (int i = 0; i < cols.length; i++) {
                String col = cols[i].trim();
                if (col.equalsIgnoreCase("CheckingAccount")) checkingColIndex   = i;
                if (col.equalsIgnoreCase("customerID"))      customerIDColIndex = i;
            }

            if (checkingColIndex == -1 || customerIDColIndex == -1) {
                System.out.println("[CustomerInfo] Required columns not found — skipping update.");
                return;
            }

            Set<String> usersWithChecking = new HashSet<>();
            for (CheckingUser u : users) {
                if (!u.accounts.isEmpty()) usersWithChecking.add(u.userID);
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) { lines.add(line); continue; }
                String[] parts = line.split(",", -1);
                if (parts.length > customerIDColIndex) {
                    String cid = parts[customerIDColIndex].trim();
                    if (usersWithChecking.contains(cid) && parts.length > checkingColIndex) {
                        parts[checkingColIndex] = "true";
                        line = String.join(",", parts);
                    }
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("[CustomerInfo] Error reading file: " + e.getMessage());
            return;
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println(header);
            for (String l : lines) pw.println(l);
        } catch (IOException e) {
            System.out.println("[CustomerInfo] Error writing file: " + e.getMessage());
            return;
        }

        System.out.println("[CustomerInfo] CheckingAccount column updated in: " + customerInfoPath);
    }

    // --- handleUserEntry / createNewCheckingAccount ---

    public static String handleUserEntry(Scanner scanner, CheckingUser user, List<CheckingUser> loadedUsers,
                                         String checkingPath, String customerInfoPath) throws IOException {

        List<Account> activeAccounts = new ArrayList<>();
        for (Account acc : user.accounts) {
            if (acc.isActive) activeAccounts.add(acc);
        }

        if (user.accounts.isEmpty()) {
            System.out.println("No checking account found for User ID " + user.userID + ". Creating new account.");
            return createNewCheckingAccount(scanner, user, loadedUsers, checkingPath, customerInfoPath);
        }

        if (activeAccounts.isEmpty()) {
            System.out.println("----------------------------------------------");
            System.out.println("  ACCESS DENIED: All accounts for User ID");
            System.out.printf ("  %s are currently deactivated.%n", user.userID);
            System.out.println("  Please contact support for assistance.");
            System.out.println("----------------------------------------------");
            return null;
        }

        for (Account acc : user.accounts) {
            if (!acc.isActive)
                System.out.printf("[WARNING] Account %s is deactivated and cannot be used.%n", acc.accountID);
        }

        if (activeAccounts.size() == 1) {
            System.out.printf("Logged into account %s (Balance: $%.2f)%n",
                    activeAccounts.get(0).accountID, activeAccounts.get(0).balance);
            return activeAccounts.get(0).accountID;
        }

        System.out.println("\nYou have multiple checking accounts. Which would you like to access?");
        for (int i = 0; i < activeAccounts.size(); i++) {
            System.out.printf("  %d. %s  ($%.2f)%n", i + 1,
                    activeAccounts.get(i).accountID, activeAccounts.get(i).balance);
        }
        System.out.printf("  %d. Open a new checking account%n", activeAccounts.size() + 1);
        System.out.print("Enter your choice: ");

        int choice;
        try { choice = scanner.nextInt(); scanner.nextLine(); }
        catch (InputMismatchException e) { scanner.nextLine(); System.out.println("Invalid input, defaulting to first account."); return activeAccounts.get(0).accountID; }

        if (choice == activeAccounts.size() + 1) {
            return createNewCheckingAccount(scanner, user, loadedUsers, checkingPath, customerInfoPath);
        } else if (choice >= 1 && choice <= activeAccounts.size()) {
            String selected = activeAccounts.get(choice - 1).accountID;
            System.out.printf("Logged into account %s%n", selected);
            return selected;
        } else {
            System.out.println("Invalid choice, defaulting to first account.");
            return activeAccounts.get(0).accountID;
        }
    }

    public static String createNewCheckingAccount(Scanner scanner, CheckingUser user,
                                                  List<CheckingUser> loadedUsers, String checkingPath, String customerInfoPath) throws IOException {
        System.out.print("Initial deposit amount: $");
        double initialDeposit = 0;
        try { initialDeposit = scanner.nextDouble(); scanner.nextLine(); }
        catch (InputMismatchException e) { scanner.nextLine(); System.out.println("Invalid amount, starting with $0.00."); }

        if (initialDeposit < 0) { System.out.println("Deposit cannot be negative. Starting with $0.00."); initialDeposit = 0; }

        System.out.print("Would you like overdraft protection? (yes/no): ");
        String odInput = scanner.nextLine().trim().toLowerCase();
        boolean overdraft = odInput.equals("yes") || odInput.equals("y");

        String newID = generateID(loadedUsers);
        Account newAccount = new Account(newID, initialDeposit);
        user.addAccount(newAccount);
        user.hasOverdraftProtection = overdraft;

        if (initialDeposit > 0) newAccount.addTransaction("Initial Deposit", initialDeposit);

        writeCSV(checkingPath, loadedUsers);
        updateCustomerInfoCSV(customerInfoPath, loadedUsers);
        System.out.printf("New checking account created: %s (Balance: $%.2f) | Overdraft Protection: %s%n",
                newID, initialDeposit, overdraft ? "Enabled" : "Disabled");
        return newID;
    }

    // --- Transaction Menu (extracted from main so launch() can call it) ---

    private static void runTransactionMenu(Scanner sc, CheckingUser user,
                                           List<CheckingUser> loadedUsers, String checkingPath, String savingsPath) throws IOException {

        String customerInfoPath = "customerInfo.csv";
        String selectedAccount  = handleUserEntry(sc, user, loadedUsers, checkingPath, customerInfoPath);
        if (selectedAccount == null) return;

        System.out.println("\nWelcome, " + user.name + "!");

        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\nMenu Options");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer between accounts");
            System.out.println("4. View transaction history");
            System.out.println("5. Show balances");
            System.out.println("6. Logout");
            System.out.print("\nPlease enter your selection: ");

            int select;
            try { select = sc.nextInt(); sc.nextLine(); }
            catch (InputMismatchException e) { sc.nextLine(); System.out.println("Invalid input. Please enter a number 1-6."); continue; }

            if (select == 1) {
                System.out.print("Please enter the amount you want to deposit: ");
                double deposit = sc.nextDouble(); sc.nextLine();
                if (deposit <= 0) { System.out.println("Please enter an amount greater than 0."); }
                else { user.deposit(selectedAccount, deposit); System.out.println("$" + deposit + " was successfully deposited."); writeCSV(checkingPath, loadedUsers); }

            } else if (select == 2) {
                System.out.print("Please enter the amount you want to withdraw: ");
                double withdraw = sc.nextDouble(); sc.nextLine();
                if (withdraw <= 0) { System.out.println("Amount must be > 0"); }
                else { user.withdraw(selectedAccount, withdraw); writeCSV(checkingPath, loadedUsers); }

            } else if (select == 3) {
                System.out.println("Transfer type: ");
                System.out.println("  1. Checking → Checking");
                System.out.println("  2. Checking → Savings");
                System.out.print("Enter transfer type: ");
                int transferType = sc.nextInt(); sc.nextLine();

                if (transferType == 1) {
                    if (user.accounts.size() < 2) { System.out.println("You need at least 2 checking accounts to transfer between them."); }
                    else {
                        System.out.println("Your checking accounts: ");
                        for (int i = 0; i < user.accounts.size(); i++)
                            System.out.printf("  %d. %s ($%.2f) [%s]%n", i + 1,
                                    user.accounts.get(i).accountID, user.accounts.get(i).balance,
                                    user.accounts.get(i).isActive ? "Active" : "Inactive");
                        System.out.print("Enter source account ID: ");      String fromID = sc.nextLine();
                        System.out.print("Enter destination account ID: "); String toID   = sc.nextLine();
                        System.out.print("Enter amount to transfer: ");     double transferAmt = sc.nextDouble(); sc.nextLine();
                        if (transferAmt <= 0) { System.out.println("Amount must be > 0"); }
                        else { user.transfer(fromID, toID, transferAmt); writeCSV(checkingPath, loadedUsers); }
                    }

                } else if (transferType == 2) {
                    if (user.savingsAccount == null) { System.out.println("No savings account linked to your profile."); }
                    else {
                        System.out.println("Your checking accounts: ");
                        for (int i = 0; i < user.accounts.size(); i++)
                            System.out.printf("  %d. %s ($%.2f) [%s]%n", i + 1,
                                    user.accounts.get(i).accountID, user.accounts.get(i).balance,
                                    user.accounts.get(i).isActive ? "Active" : "Inactive");
                        System.out.printf("Your savings account: %s ($%.2f)%n",
                                user.savingsAccount.accountID, user.savingsAccount.balance);
                        System.out.print("Enter source checking account ID: "); String fromID = sc.nextLine();
                        System.out.print("Enter amount to transfer: ");         double transferAmt = sc.nextDouble(); sc.nextLine();
                        if (transferAmt <= 0) { System.out.println("Amount must be > 0"); }
                        else { user.transferToSavings(fromID, transferAmt); writeCSV(checkingPath, loadedUsers); writeSavingsCSV(savingsPath, loadedUsers); }
                    }
                } else { System.out.println("Invalid transfer type. Please enter 1 or 2."); }

            } else if (select == 4) {
                for (Account acc : user.accounts) {
                    if (acc.accountID.equals(selectedAccount)) { acc.printTransactionHistory(); break; }
                }
            } else if (select == 5) {
                System.out.println("\n--- Your Accounts ---");
                user.printAccounts();
            } else if (select == 6) {
                System.out.println("Logged out successfully. Returning to main menu.");
                writeCSV(checkingPath, loadedUsers);
                writeSavingsCSV(savingsPath, loadedUsers);
                loggedIn = false;
            } else { System.out.println("Invalid selection. Please choose 1-6."); }
        }
    }

    // --- Launch (called by BankApp's menu.java) ---

    // "appUser" here is BankApp's User.java — no naming conflict since CheckingUser is our inner class name
    public static void launch(Scanner sc, User appUser) throws IOException {
        String checkingPath     = "checking_accounts.csv";
        String savingsPath      = "Savings.csv";
        String customerInfoPath = "customerInfo.csv";

        List<CheckingUser> loadedUsers = readCSV(checkingPath);

        try {
            readSavingsCSV(savingsPath, loadedUsers);
        } catch (FileNotFoundException e) {
            // savings not required to run checking
        }

        for (CheckingUser u : loadedUsers) u.applyExpiredMinBalanceFees();

        // Find the CheckingUser that matches the logged-in BankApp user by customerID
        CheckingUser checkUser = findUser(loadedUsers, appUser.customerID);
        if (checkUser == null) {
            // First time this user enters the checking module — create their record
            checkUser = new CheckingUser(appUser.customerID, appUser.firstName + " " + appUser.lastName);
            // If they already had a balance in customerInfo.csv, seed it as their first account
            if (appUser.checkingAccount > 0) {
                Account existing = new Account(appUser.customerID + "-CHK", appUser.checkingAccount);
                checkUser.addAccount(existing);
            }
            loadedUsers.add(checkUser);
            writeCSV(checkingPath, loadedUsers);
        }

        runTransactionMenu(sc, checkUser, loadedUsers, checkingPath, savingsPath);

        // Write the final balance back to BankApp's user so UserDatabase stays in sync
        appUser.checkingAccount = checkUser.getTotalBalance();
        updateCustomerInfoCSV(customerInfoPath, loadedUsers);
    }

    // --- Main (standalone entry point, still works independently) ---

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        List<CheckingUser> users = new ArrayList<>();

        CheckingUser alice = new CheckingUser("1001", "Alice Johnson");
        alice.addAccount(new Account("415631219101", 4250.75));
        alice.addAccount(new Account("498891668177", 1800.00));
        alice.addAccount(new Account("431246013015",  620.50));

        CheckingUser bob = new CheckingUser("1002", "Bob Martinez");
        bob.addAccount(new Account("418138552030", 3100.00));
        bob.addAccount(new Account("416048021673", 5500.00));

        CheckingUser carol = new CheckingUser("1003", "Carol Smith");
        carol.addAccount(new Account("476846326096",  980.25));
        carol.addAccount(new Account("477682810754", 2200.00));
        carol.addAccount(new Account("406107107737",  750.00));
        carol.addAccount(new Account("408717912686", 8400.00));

        CheckingUser david = new CheckingUser("29399", "David Lee");

        users.add(alice); users.add(bob); users.add(carol); users.add(david);

        String checkingPath     = "checking_accounts.csv";
        String savingsPath      = "Savings.csv";
        String customerInfoPath = "customerInfo.csv";

        writeCSV(checkingPath, users);
        List<CheckingUser> loadedUsers = readCSV(checkingPath);

        try {
            readSavingsCSV(savingsPath, loadedUsers);
        } catch (FileNotFoundException e) {
            System.out.println("Savings.csv not found. No savings accounts will be loaded.");
        }

        updateCustomerInfoCSV(customerInfoPath, loadedUsers);

        System.out.println("\n--- Checking for expired minimum-balance warnings on startup ---");
        for (CheckingUser u : loadedUsers) u.applyExpiredMinBalanceFees();

        boolean running = true;
        while (running) {
            System.out.println("\n=============================");
            System.out.println("   Welcome to the Bank");
            System.out.println("=============================");
            System.out.print("Enter your User ID (or 'exit' to quit): ");
            String inputID = scanner.next().trim();
            scanner.nextLine();

            if (inputID.equalsIgnoreCase("exit")) {
                System.out.println("Thank you for visiting. Goodbye!");
                break;
            }

            CheckingUser user = findUser(loadedUsers, inputID);
            if (user == null) { System.out.println("User ID not found. Please try again."); continue; }

            runTransactionMenu(scanner, user, loadedUsers, checkingPath, savingsPath);
        }

        writeCSV(checkingPath, loadedUsers);
        writeSavingsCSV(savingsPath, loadedUsers);
        scanner.close();
    }
}