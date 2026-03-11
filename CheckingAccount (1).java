import java.time.*;
import java.time.format.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.io.*; 


public class BankingCSV {

    static class Account {
        String accountID;
        double balance;
        boolean belowMinBalance;
        boolean isActive;
        LocalDate wentNegativeDate;
        LocalDateTime minBalWarnedAt;  // when balance first dropped below MIN_BALANCE
        
        //Transaction History: 2d array
        //rows = single transaction, columns = transaction name, amount, date, balance after
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
            if (transactionCount==0) {
                System.out.println("No transactions yet.");
                return; 
            }
            for (int i = 0; i < transactionCount; i++) { 
                System.out.println("Type: " + transactionHistory[i][0]); 
                System.out.println("Amount:$ " + transactionHistory[i][1]); 
                System.out.println("Date: " + transactionHistory[i][2]); 
                System.out.println("Balance After:$ " + transactionHistory[i][3]); 
            }
        }        

        Account(String accountID, double balance) {
            this.accountID        = accountID;
            this.balance          = balance;
            this.belowMinBalance  = balance < MIN_BALANCE;
            this.isActive         = true;
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
                    if (daysNegative >= 30) {
                        this.isActive = false;
                    }
                }
            } else {
                this.wentNegativeDate = null;
                if (!this.isActive && this.balance >= 0) {
                    this.isActive = true;
                }
            }

            // Clear min-balance warning if balance recovered
            if (this.balance >= MIN_BALANCE && this.minBalWarnedAt != null) {
                System.out.printf("[Min-Balance] Account %s is back above $%.2f. Warning cleared.%n",
                        this.accountID, MIN_BALANCE);
                this.minBalWarnedAt = null;
            }
        }

        // Issues warning or charges fee depending on how long balance has been below minimum.
        // Returns true if a fee was charged.
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
                this.minBalWarnedAt = LocalDateTime.now(); // reset so fee recurs every 24 hrs if still below
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

    static class User {
        String userID;
        String name;
        List<Account> accounts;
        boolean hasOverdraftProtection;
        SavingsAccount savingsAccount;

        User(String userID, String name) {
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

        // Called on startup — charges fee for any account whose 24-hour window already passed
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
                        acc.updateFlags();
                        System.out.printf("Withdrew $%.2f from %s. New balance: $%.2f%n", amount, accountID, acc.balance);
                    } else {
                        double shortfall = amount - acc.balance;
                        if (hasOverdraftProtection && savingsAccount != null) {
                            if (savingsAccount.balance >= shortfall) {
                                savingsAccount.balance -= shortfall;
                                acc.balance = 0;
                                acc.updateFlags();
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
            if (from == null)    { System.out.println("Source account not found: " + fromAccountID); return; }
            if (to   == null)    { System.out.println("Destination account not found: " + toAccountID); return; }
            if (!from.isActive)  { System.out.println("Source account " + fromAccountID + " is inactive."); return; }
            if (!to.isActive)    { System.out.println("Destination account " + toAccountID + " is inactive."); return; }
            if (amount > from.balance) { System.out.println("Insufficient funds in " + fromAccountID); return; }
            from.balance -= amount;
            to.balance   += amount;
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
                String negSince = acc.wentNegativeDate != null ? acc.wentNegativeDate.toString() : "N/A";
                String warnInfo = "";
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

    public static Set<String> getAllIDs(List<User> users) {
        Set<String> existing = new HashSet<>();
        for (User user : users)
            for (Account acc : user.accounts)
                existing.add(acc.accountID);
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
    // Columns: User ID, Full Name, Account ID, Balance, Has Overdraft Protection,
    //          Below Min Balance, Is Active, Went Negative Date, Min Bal Warned At

    static String[] headers = {
        "User ID", "Full Name", "Account ID", "Balance",
        "Has Overdraft Protection", "Below Min Balance", "Is Active",
        "Went Negative Date", "Min Bal Warned At"
    };

    public static void writeCSV(String filepath, List<User> users) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filepath))) {
            pw.println(String.join(",", headers));
            for (User user : users) {
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
        System.out.println("CSV written to: " + filepath);
    }

    public static List<User> readCSV(String filepath) throws IOException {
        Map<String, User> userMap = new LinkedHashMap<>();
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

                userMap.putIfAbsent(userID, new User(userID, name));
                User user = userMap.get(userID);
                user.hasOverdraftProtection = hasOverdraft;
                user.addAccount(new Account(accountID, balance, belowMin, isActive, negDate, warnedAt));
            }
        }
        return new ArrayList<>(userMap.values());
    }

    public static User findUser(List<User> users, String userID) {
        for (User u : users) if (u.userID.equals(userID)) return u;
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

    public static void readSavingsCSV(String filepath, List<User> users) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts   = line.split(",");
                String userID    = parts[0];
                String accountID = parts[2];
                double balance   = Double.parseDouble(parts[3]);
                User user = findUser(users, userID);
                if (user != null) user.savingsAccount = new SavingsAccount(accountID, userID, balance);
            }
        }
    }

    // --- Main ---

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        List<User> users = new ArrayList<>();

        User alice = new User("1001", "Alice Johnson");
        alice.addAccount(new Account("415631219101", 4250.75));
        alice.addAccount(new Account("498891668177", 1800.00));
        alice.addAccount(new Account("431246013015",  620.50));

        User bob = new User("1002", "Bob Martinez");
        bob.addAccount(new Account("418138552030", 3100.00));
        bob.addAccount(new Account("416048021673", 5500.00));

        User carol = new User("1003", "Carol Smith");
        carol.addAccount(new Account("476846326096",  980.25));
        carol.addAccount(new Account("477682810754", 2200.00));
        carol.addAccount(new Account("406107107737",  750.00));
        carol.addAccount(new Account("408717912686", 8400.00));

        User david = new User("1004", "David Lee");
        david.addAccount(new Account("431003814027", 1500.00));

        users.add(alice); users.add(bob); users.add(carol); users.add(david);

        String checkingPath = "banking_accounts.csv";
        String savingsPath  = "banking_savings.csv";
        writeCSV(checkingPath, users);
        List<User> loadedUsers = readCSV(checkingPath);

        findUser(loadedUsers, "1001").savingsAccount = new SavingsAccount("SAV001", "1001", 3200.00);
        findUser(loadedUsers, "1002").savingsAccount = new SavingsAccount("SAV002", "1002", 8750.50);
        findUser(loadedUsers, "1003").savingsAccount = new SavingsAccount("SAV003", "1003",  500.00);
        findUser(loadedUsers, "1004").savingsAccount = new SavingsAccount("SAV004", "1004", 1100.00);

        writeSavingsCSV(savingsPath, loadedUsers);
        readSavingsCSV(savingsPath, loadedUsers);

        // On startup: apply any fees whose 24-hour grace period already expired
        System.out.println("\n--- Checking for expired minimum-balance warnings on startup ---");
        for (User u : loadedUsers) u.applyExpiredMinBalanceFees();

        System.out.println("\n--- Adding New User: Eve Turner ---");
        User eve = new User("1005", "Eve Turner");
        eve.addAccount(new Account(generateID(loadedUsers), 2000.00));
        loadedUsers.add(eve);
        eve.addAccount(new Account(generateID(loadedUsers), 500.00));
        findUser(loadedUsers, "1005").printAccounts();

        User user = findUser(loadedUsers, "1001");
        String selectedAccount = user.accounts.get(0).accountID;
        user.hasOverdraftProtection = true;

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

            if (select == 1) {
                System.out.print("Please enter the amount you want to deposit: ");
                double deposit = scanner.nextDouble();
                if (deposit <= 0) { System.out.println("Please enter an amount greater than 0."); }
                else { user.deposit(selectedAccount, deposit); System.out.println("$" + deposit + " was successfully deposited."); writeCSV(checkingPath, loadedUsers); }

            } else if (select == 2) {
                System.out.print("Please enter the amount you want to withdraw: ");
                double withdraw = scanner.nextDouble();
                if (withdraw <= 0) { System.out.println("Amount must be > 0"); }
                else { user.withdraw(selectedAccount, withdraw); writeCSV(checkingPath, loadedUsers); }

            } else if (select == 3) {
                System.out.println("Transfer type:");
                System.out.println("  1. Checking \u2192 Checking");
                System.out.println("  2. Checking \u2192 Savings");
                System.out.print("Enter transfer type: ");
                int transferType = scanner.nextInt();

                if (transferType == 1) {
                    if (user.accounts.size() < 2) { System.out.println("You need at least 2 checking accounts to transfer between them."); }
                    else {
                        System.out.println("Your checking accounts:");
                        for (int i = 0; i < user.accounts.size(); i++)
                            System.out.printf("  %d. %s ($%.2f) [%s]%n", i + 1,
                                user.accounts.get(i).accountID, user.accounts.get(i).balance,
                                user.accounts.get(i).isActive ? "Active" : "Inactive");
                        System.out.print("Enter source account ID: ");      String fromID = scanner.next();
                        System.out.print("Enter destination account ID: "); String toID   = scanner.next();
                        System.out.print("Enter amount to transfer: ");     double transferAmt = scanner.nextDouble();
                        if (transferAmt <= 0) { System.out.println("Amount must be > 0"); }
                        else { user.transfer(fromID, toID, transferAmt); writeCSV(checkingPath, loadedUsers); }
                    }

                } else if (transferType == 2) {
                    if (user.savingsAccount == null) { System.out.println("No savings account linked to your profile."); }
                    else {
                        System.out.println("Your checking accounts:");
                        for (int i = 0; i < user.accounts.size(); i++)
                            System.out.printf("  %d. %s ($%.2f) [%s]%n", i + 1,
                                user.accounts.get(i).accountID, user.accounts.get(i).balance,
                                user.accounts.get(i).isActive ? "Active" : "Inactive");
                        System.out.printf("Your savings account: %s ($%.2f)%n",
                            user.savingsAccount.accountID, user.savingsAccount.balance);
                        System.out.print("Enter source checking account ID: "); String fromID = scanner.next();
                        System.out.print("Enter amount to transfer: ");         double transferAmt = scanner.nextDouble();
                        if (transferAmt <= 0) { System.out.println("Amount must be > 0"); }
                        else { user.transferToSavings(fromID, transferAmt); writeCSV(checkingPath, loadedUsers); writeSavingsCSV(savingsPath, loadedUsers); }
                    }
                } else { System.out.println("Invalid transfer type. Please enter 1 or 2."); }

            } else if (select == 4) { System.out.println("Transaction history is not yet implemented.");
            } else if (select == 5) { System.out.println("\n--- Your Accounts ---"); user.printAccounts();
            } else if (select == 6) { System.out.println("Goodbye!"); on = false;
            } else { System.out.println("Invalid selection. Please choose 1-6."); }
        }

        writeCSV(checkingPath, loadedUsers);
        writeSavingsCSV(savingsPath, loadedUsers);
        scanner.close();
    }
}
