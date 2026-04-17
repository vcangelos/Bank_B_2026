//RothIRA.loadFromCSV(); on startup for connecters
// RothIRA.manageRothIRA(scanner, customerID); for the main menu
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.*;
import java.io.IOException;

public class RothIRA {

    // Account properties
    private String accountID;
    private String customerID;
    private double balance;
    private double totalContributionsThisYear;
    private int yearOpened;
    private LocalDate dateOfBirth;
    private String investmentType;
    private double interestRate;
    private LocalDateTime lastUpdated;
    private String status;

    // Constants
    private static final double CONTRIBUTION_LIMIT_REGULAR = 7000.00;
    private static final double CONTRIBUTION_LIMIT_CATCHUP = 8000.00; // age 50+
    private static final double WITHDRAWAL_PENALTY_RATE = 0.10;
    private static final double MINIMUM_OPENING_DEPOSIT = 500.00;
    private static final int RETIREMENT_AGE = 59;
    private static final int FIVE_YEAR_RULE = 5;

    // Investment options
    private static final Map<String, Double> INVESTMENT_OPTIONS = new LinkedHashMap<>();
    static {
        INVESTMENT_OPTIONS.put("Stocks", 0.07);
        INVESTMENT_OPTIONS.put("Bonds", 0.04);
        INVESTMENT_OPTIONS.put("Money Market", 0.02);
    }

    // CSV paths
    private static final Path ROTH_IRA_CSV_PATH = Path.of("RothIRA.csv");
    private static final Path CUSTOMER_CSV_PATH = Path.of("customerInfo.csv");

    // Track all accounts
    private static Map<String, RothIRA> allAccounts = new HashMap<>();

    // Account counter
    private static int nextAccountNumber = 1;

    // Date formatters
    private static final DateTimeFormatter DOB_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final DateTimeFormatter CSV_DT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME; // for saving/loading
    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // for display

    public RothIRA(String accountID, String customerID, double balance, double totalContributionsThisYear,
                   int yearOpened, LocalDate dateOfBirth, String investmentType, double interestRate,
                   LocalDateTime lastUpdated, String status) {
        this.accountID = accountID;
        this.customerID = customerID;
        this.balance = balance;
        this.totalContributionsThisYear = totalContributionsThisYear;
        this.yearOpened = yearOpened;
        this.dateOfBirth = dateOfBirth;
        this.investmentType = investmentType;
        this.interestRate = interestRate;
        this.lastUpdated = lastUpdated;
        this.status = status;
    }

    // ===================== CSV METHODS =====================

    private static String generateAccountID() {
        String id = "RIRA" + String.format("%03d", nextAccountNumber);
        nextAccountNumber++;
        return id;
    }

    public static void loadFromCSV() {
        try {
            if (!Files.exists(ROTH_IRA_CSV_PATH)) {
                String header = "accountID,customerID,balance,totalContributionsThisYear,yearOpened,dateOfBirth,investmentType,interestRate,lastUpdated,status";
                Files.write(ROTH_IRA_CSV_PATH, Arrays.asList(header));
                System.out.println("Created new RothIRA.csv");
                return;
            }

            List<String> lines = Files.readAllLines(ROTH_IRA_CSV_PATH);

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(",", -1);

                if (fields.length >= 10) {
                    String accountID    = fields[0];
                    String customerID   = fields[1];
                    double balance      = Double.parseDouble(fields[2]);
                    double contributions = Double.parseDouble(fields[3]);
                    int yearOpened      = Integer.parseInt(fields[4]);
                    LocalDate dob       = LocalDate.parse(fields[5], DOB_FORMATTER);
                    String investType   = fields[6].replace("\"", "");
                    double rate         = Double.parseDouble(fields[7]);
                    LocalDateTime lastUp = LocalDateTime.parse(fields[8], CSV_DT_FORMATTER);
                    String status       = fields[9];

                    RothIRA account = new RothIRA(accountID, customerID, balance, contributions,
                            yearOpened, dob, investType, rate, lastUp, status);

                    allAccounts.put(accountID, account);

                    String numPart = accountID.substring(4); // Remove "RIRA"
                    int num = Integer.parseInt(numPart);
                    if (num >= nextAccountNumber) {
                        nextAccountNumber = num + 1;
                    }
                }
            }

            System.out.println("Loaded " + allAccounts.size() + " Roth IRA account(s) from CSV");

        } catch (IOException e) {
            System.out.println("Warning: Could not load Roth IRA accounts from CSV");
        }
    }

    private static void saveToCSV() {
        try {
            List<String> lines = new ArrayList<>();
            lines.add("accountID,customerID,balance,totalContributionsThisYear,yearOpened,dateOfBirth,investmentType,interestRate,lastUpdated,status");

            for (RothIRA account : allAccounts.values()) {
                String line = String.format("%s,%s,%.2f,%.2f,%d,%s,\"%s\",%.2f,%s,%s",
                        account.accountID,
                        account.customerID,
                        account.balance,
                        account.totalContributionsThisYear,
                        account.yearOpened,
                        account.dateOfBirth.format(DOB_FORMATTER),
                        account.investmentType,
                        account.interestRate,
                        account.lastUpdated.format(CSV_DT_FORMATTER),
                        account.status
                );
                lines.add(line);
            }

            Files.write(ROTH_IRA_CSV_PATH, lines);

        } catch (IOException e) {
            System.out.println("Error: Could not save Roth IRA accounts to CSV");
        }
    }

    // ===================== HELPER METHODS =====================

    private int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    private double getContributionLimit() {
        return getAge() >= 50 ? CONTRIBUTION_LIMIT_CATCHUP : CONTRIBUTION_LIMIT_REGULAR;
    }

    private boolean isRetirementAge() {
        return getAge() >= RETIREMENT_AGE;
    }

    private boolean meetsFiveYearRule() {
        return (LocalDate.now().getYear() - yearOpened) >= FIVE_YEAR_RULE;
    }

    private static RothIRA findAccountByCustomer(String customerID) {
        for (RothIRA account : allAccounts.values()) {
            if (account.customerID.equals(customerID) && account.status.equals("Active")) {
                return account;
            }
        }
        return null;
    }

    // Reset yearly contributions if it's a new year
    private void checkAndResetYearlyContributions() {
        int currentYear = LocalDate.now().getYear();
        if (lastUpdated.getYear() < currentYear) {
            totalContributionsThisYear = 0.00;
            lastUpdated = LocalDateTime.now();
            saveToCSV();
            System.out.println("✓ New year detected — contribution limit has been reset for " + currentYear);
        }
    }

    // ===================== CORE FEATURES =====================

    // Open a new Roth IRA account
    public static RothIRA openAccount(Scanner scanner, String customerID) {
        try {
            // Verify customer exists
            csvFile customerFile = new csvFile(CUSTOMER_CSV_PATH);
            Map<String, String> customerRecord = customerFile.getRecord("customerID", customerID);

            if (customerRecord == null) {
                System.out.println("Error: Customer not found");
                return null;
            }

            // Check if they already have an account
            if (findAccountByCustomer(customerID) != null) {
                System.out.println("Error: You already have a Roth IRA account");
                return null;
            }

            String firstName = customerRecord.get("firstName");
            String lastName = customerRecord.get("lastName");
            String dobStr = customerRecord.get("DOB");

            LocalDate dob = LocalDate.parse(dobStr, DOB_FORMATTER);
            int age = Period.between(dob, LocalDate.now()).getYears();

            System.out.println("\n========================================");
            System.out.println("        Open a Roth IRA Account");
            System.out.println("========================================");
            System.out.println("Customer: " + firstName + " " + lastName);
            System.out.println("Age: " + age);
            System.out.println("Minimum Opening Deposit: $" + MINIMUM_OPENING_DEPOSIT);

            // Show investment options
            System.out.println("\nAvailable Investment Types:");
            int i = 1;
            for (Map.Entry<String, Double> option : INVESTMENT_OPTIONS.entrySet()) {
                System.out.printf("  %d. %-15s %.0f%% annual return%n", i, option.getKey(), option.getValue() * 100);
                i++;
            }

            System.out.print("\nSelect investment type (1-3): ");
            int choice = scanner.nextInt();

            if (choice < 1 || choice > 3) {
                System.out.println("Invalid selection");
                return null;
            }

            List<String> keys = new ArrayList<>(INVESTMENT_OPTIONS.keySet());
            String selectedType = keys.get(choice - 1);
            double selectedRate = INVESTMENT_OPTIONS.get(selectedType);

            System.out.print("\nEnter opening deposit amount: $");
            double deposit = scanner.nextDouble();

            if (deposit < MINIMUM_OPENING_DEPOSIT) {
                System.out.println("Error: Minimum opening deposit is $" + MINIMUM_OPENING_DEPOSIT);
                return null;
            }

            double limit = age >= 50 ? CONTRIBUTION_LIMIT_CATCHUP : CONTRIBUTION_LIMIT_REGULAR;
            if (deposit > limit) {
                System.out.println("Error: Opening deposit cannot exceed annual contribution limit of $" + limit);
                return null;
            }

            String accountID = generateAccountID();
            int yearOpened = LocalDate.now().getYear();

            RothIRA newAccount = new RothIRA(accountID, customerID, deposit, deposit,
                    yearOpened, dob, selectedType, selectedRate, LocalDateTime.now(), "Active");

            allAccounts.put(accountID, newAccount);
            saveToCSV();

            System.out.println("\n✓ Roth IRA account opened successfully!");
            System.out.println("  Account ID:      " + accountID);
            System.out.println("  Investment Type: " + selectedType);
            System.out.println("  Annual Return:   " + (int)(selectedRate * 100) + "%");
            System.out.println("  Opening Balance: $" + String.format("%.2f", deposit));
            System.out.println("  Contribution Limit Remaining: $" + String.format("%.2f", limit - deposit));

            return newAccount;

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    // Make a contribution
    public void makeContribution(Scanner scanner) {
        checkAndResetYearlyContributions();

        double limit = getContributionLimit();
        double remaining = limit - totalContributionsThisYear;

        System.out.println("\n=== Make a Contribution ===");
        System.out.println("Annual Contribution Limit: $" + String.format("%.2f", limit));
        System.out.println("Contributed This Year:     $" + String.format("%.2f", totalContributionsThisYear));
        System.out.println("Remaining Limit:           $" + String.format("%.2f", remaining));

        if (remaining <= 0) {
            System.out.println("Error: You have reached your annual contribution limit of $" + limit);
            return;
        }

        System.out.print("\nEnter contribution amount: $");
        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Error: Contribution must be greater than $0");
            return;
        }

        if (amount > remaining) {
            System.out.println("Error: Contribution of $" + String.format("%.2f", amount) +
                    " would exceed your remaining limit of $" + String.format("%.2f", remaining));
            System.out.println("Maximum you can contribute right now: $" + String.format("%.2f", remaining));
            return;
        }

        balance += amount;
        totalContributionsThisYear += amount;
        lastUpdated = LocalDateTime.now();
        saveToCSV();

        System.out.println("\n✓ Contribution successful!");
        System.out.println("  Amount Added:    $" + String.format("%.2f", amount));
        System.out.println("  New Balance:     $" + String.format("%.2f", balance));
        System.out.println("  Remaining Limit: $" + String.format("%.2f", limit - totalContributionsThisYear));
        System.out.println("\nNote: Please ensure funds are transferred from your bank account separately.");
    }

    // View balance and growth
    public void viewBalanceAndGrowth() {
        checkAndResetYearlyContributions();

        double limit = getContributionLimit();
        double projectedOneYear  = balance * (1 + interestRate);
        double projectedFiveYear = balance * Math.pow(1 + interestRate, 5);
        double projectedTenYear  = balance * Math.pow(1 + interestRate, 10);

        System.out.println("\n=== Roth IRA Balance & Growth ===");
        System.out.println("Account ID:       " + accountID);
        System.out.println("Investment Type:  " + investmentType);
        System.out.println("Annual Rate:      " + (int)(interestRate * 100) + "%");
        System.out.println("Current Balance:  $" + String.format("%.2f", balance));
        System.out.println("Last Updated:     " + lastUpdated.format(DT_FORMATTER));
        System.out.println("\n--- Contribution Tracker ---");
        System.out.println("Contributed This Year: $" + String.format("%.2f", totalContributionsThisYear));
        System.out.println("Annual Limit:          $" + String.format("%.2f", limit));
        System.out.println("Remaining:             $" + String.format("%.2f", limit - totalContributionsThisYear));
        System.out.println("\n--- Projected Growth ---");
        System.out.println("1 Year:  $" + String.format("%.2f", projectedOneYear));
        System.out.println("5 Years: $" + String.format("%.2f", projectedFiveYear));
        System.out.println("10 Years: $" + String.format("%.2f", projectedTenYear));
        System.out.println("\n--- Withdrawal Eligibility ---");
        System.out.println("Age:             " + getAge());
        System.out.println("Retirement Age:  " + RETIREMENT_AGE + "+");
        System.out.println("5-Year Rule Met: " + (meetsFiveYearRule() ? "Yes" : "No (opened " + yearOpened + ")"));
        System.out.println("Penalty-Free:    " + (isRetirementAge() && meetsFiveYearRule() ? "Yes ✓" : "No ✗"));
    }

    // Withdraw funds
    public void withdraw(Scanner scanner) {
        System.out.println("\n=== Withdraw Funds ===");
        System.out.println("Current Balance: $" + String.format("%.2f", balance));
        System.out.println("Your Age: " + getAge());

        boolean penaltyFree = isRetirementAge() && meetsFiveYearRule();

        if (!penaltyFree) {
            System.out.println("\n⚠ WARNING: Early Withdrawal Penalties Apply!");
            if (!isRetirementAge()) {
                System.out.println("  - You are under " + RETIREMENT_AGE + " years old");
                System.out.println("  - A " + (int)(WITHDRAWAL_PENALTY_RATE * 100) + "% early withdrawal penalty will apply");
            }
            if (!meetsFiveYearRule()) {
                System.out.println("  - Your account has not met the 5-year rule (opened " + yearOpened + ")");
            }
            System.out.println("  - Withdrawn amount may also be subject to income tax");
            System.out.print("\nDo you still want to proceed? (yes/no): ");
            String confirm = scanner.next();
            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("Withdrawal cancelled");
                return;
            }
        }

        System.out.print("\nEnter withdrawal amount: $");
        double amount = scanner.nextDouble();

        if (amount <= 0) {
            System.out.println("Error: Withdrawal must be greater than $0");
            return;
        }

        if (amount > balance) {
            System.out.println("Error: Insufficient funds. Balance: $" + String.format("%.2f", balance));
            return;
        }

        double penalty = 0.00;
        double actualAmount = amount;

        if (!penaltyFree) {
            penalty = amount * WITHDRAWAL_PENALTY_RATE;
            actualAmount = amount - penalty;
        }

        balance -= amount;
        lastUpdated = LocalDateTime.now();
        saveToCSV();

        System.out.println("\n✓ Withdrawal processed!");
        System.out.println("  Requested Amount: $" + String.format("%.2f", amount));
        if (penalty > 0) {
            System.out.println("  Penalty (10%):    $" + String.format("%.2f", penalty));
            System.out.println("  Amount Received:  $" + String.format("%.2f", actualAmount));
        }
        System.out.println("  Remaining Balance: $" + String.format("%.2f", balance));
        System.out.println("\nNote: Please consult a tax advisor regarding tax implications.");
    }

    // Apply annual growth to balance
    public void applyGrowth() {
        double growth = balance * interestRate;
        balance += growth;
        lastUpdated = LocalDateTime.now();
        saveToCSV();

        System.out.println("\n✓ Annual growth applied!");
        System.out.println("  Investment Type: " + investmentType);
        System.out.println("  Growth Rate:     " + (int)(interestRate * 100) + "%");
        System.out.println("  Growth Amount:   $" + String.format("%.2f", growth));
        System.out.println("  New Balance:     $" + String.format("%.2f", balance));
    }

    // Change investment type
    public void changeInvestmentType(Scanner scanner) {
        System.out.println("\n=== Change Investment Type ===");
        System.out.println("Current Type: " + investmentType + " (" + (int)(interestRate * 100) + "% annual return)");

        System.out.println("\nAvailable Investment Types:");
        int i = 1;
        for (Map.Entry<String, Double> option : INVESTMENT_OPTIONS.entrySet()) {
            System.out.printf("  %d. %-15s %.0f%% annual return%n", i, option.getKey(), option.getValue() * 100);
            i++;
        }

        System.out.print("\nSelect new investment type (1-3): ");
        int choice = scanner.nextInt();

        if (choice < 1 || choice > 3) {
            System.out.println("Invalid selection");
            return;
        }

        List<String> keys = new ArrayList<>(INVESTMENT_OPTIONS.keySet());
        String newType = keys.get(choice - 1);

        if (newType.equals(investmentType)) {
            System.out.println("You are already invested in " + investmentType);
            return;
        }

        String oldType = investmentType;
        investmentType = newType;
        interestRate = INVESTMENT_OPTIONS.get(newType);
        lastUpdated = LocalDateTime.now();
        saveToCSV();

        System.out.println("\n✓ Investment type updated!");
        System.out.println("  From: " + oldType);
        System.out.println("  To:   " + investmentType + " (" + (int)(interestRate * 100) + "% annual return)");
    }

    // Display account info
    public void displayAccountInfo() {
        System.out.println("\n=== Roth IRA Account Info ===");
        System.out.println("Account ID:      " + accountID);
        System.out.println("Customer ID:     " + customerID);
        System.out.println("Status:          " + status);
        System.out.println("Balance:         $" + String.format("%.2f", balance));
        System.out.println("Investment Type: " + investmentType);
        System.out.println("Annual Rate:     " + (int)(interestRate * 100) + "%");
        System.out.println("Year Opened:     " + yearOpened);
        System.out.println("Age:             " + getAge());
        System.out.println("Last Updated:    " + lastUpdated.format(DT_FORMATTER));
    }

    // ===================== MAIN MENU =====================

    public static void manageRothIRA(Scanner scanner, String customerID) {
        RothIRA account = findAccountByCustomer(customerID);

        if (account == null) {
            System.out.println("\nYou don't have a Roth IRA account.");
            System.out.print("Would you like to open one? (yes/no): ");
            String response = scanner.next();
            if (response.equalsIgnoreCase("yes")) {
                openAccount(scanner, customerID);
            }
            return;
        }

        // Apply growth if it's been over a year
        if (account.lastUpdated.getYear() < LocalDateTime.now().getYear()) {
            System.out.println("\nApplying annual growth to your account...");
            account.applyGrowth();
        }

        boolean continueManaging = true;

        while (continueManaging) {
            System.out.println("\n=== Roth IRA Menu ===");
            System.out.println("1. Make a contribution");
            System.out.println("2. View balance & growth");
            System.out.println("3. Withdraw funds");
            System.out.println("4. Change investment type");
            System.out.println("5. View account info");
            System.out.println("6. Exit");

            System.out.print("\nSelect option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1: account.makeContribution(scanner); break;
                case 2: account.viewBalanceAndGrowth(); break;
                case 3: account.withdraw(scanner); break;
                case 4: account.changeInvestmentType(scanner); break;
                case 5: account.displayAccountInfo(); break;
                case 6: continueManaging = false; break;
                default: System.out.println("Invalid option");
            }
        }
    }
}