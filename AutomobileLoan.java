// AutomobileLoan.java

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * AutomobileLoan.java
 *
 * Handles automobile loans for any bank customer (new or used cars).
 *
 * Features:
 * - Collects car information (make, model, year, condition)
 * - Asks for down payment and total income / total debt (debt-to-income ratio)
 * - Determines interest rate based on credit score from CreditCard
 * - Applies NJ sales tax (6.625%) to vehicle price
 * - Checks if customer already has an existing auto loan (via AutomobileLoan.csv)
 * - Calculates monthly payment using standard amortization formula
 * - Supports payment from checking (via CheckingAccount) or savings (via SavingsAccount)
 * - Tracks missed payments and delinquency status
 * - Auto pay system
 * - Saves all loan data to AutomobileLoan.csv
 */
public class AutomobileLoan {

    // ----------------------------------------------------------
    // CONSTANTS
    // ----------------------------------------------------------

    // NJ Sales Tax rate (6.625%)
    private static final double NJ_SALES_TAX = 0.06625;

    // Loan file
    private static final String FILE = "AutomobileLoan.csv";
    private static final String HEADER =
            "loanID,customerID,customerName,carMake,carModel,carYear,carCondition," +
                    "vehiclePrice,salesTax,totalVehicleCost,downPayment,principal," +
                    "interestRate,termMonths,monthlyPayment,remainingBalance," +
                    "totalPaid,paymentsMade,missedPayments,delinquency,status," +
                    "autoPayEnabled,creditScore,startDate";

    // Delinquency thresholds (matches MortgageLoan pattern)
    private static final int DAYS_30 = 1;
    private static final int DAYS_60 = 2;
    private static final int DAYS_90 = 3;

    // ----------------------------------------------------------
    // LOAN DATA FIELDS
    // ----------------------------------------------------------

    private String loanID;
    private String customerID;
    private String customerName;

    // Car info
    private String carMake;
    private String carModel;
    private int    carYear;
    private String carCondition; // "New" or "Used"

    // Financial info
    private double vehiclePrice;
    private double salesTax;
    private double totalVehicleCost;
    private double downPayment;
    private double principal;        // totalVehicleCost - downPayment
    private double interestRate;
    private int    termMonths;

    // Payment tracking
    private double monthlyPayment;
    private double remainingBalance;
    private double totalPaid;
    private int    paymentsMade;
    private int    missedPayments;
    private String delinquency;
    private String status;

    // Settings
    private boolean autoPayEnabled;
    // Credit score variable — Akhil will connect this to his credit score tracking file.
    // To integrate: replace user.creditScore with CreditScoreTracker.getScore(customerID)
    private int     creditScore;
    private LocalDate startDate;

    // ----------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------

    public AutomobileLoan(String customerID, String customerName,
                          String carMake, String carModel,
                          int carYear, String carCondition,
                          double vehiclePrice, double downPayment,
                          int termMonths, int creditScore,
                          boolean autoPayEnabled) {

        this.loanID        = generateLoanID();
        this.customerID    = customerID;
        this.customerName  = customerName;

        this.carMake       = carMake;
        this.carModel      = carModel;
        this.carYear       = carYear;
        this.carCondition  = carCondition;

        this.vehiclePrice     = round(vehiclePrice);
        this.salesTax         = round(vehiclePrice * NJ_SALES_TAX);
        this.totalVehicleCost = round(vehiclePrice + salesTax);
        this.downPayment      = round(downPayment);
        this.principal = round(this.totalVehicleCost - this.downPayment);

        this.termMonths    = termMonths;
        this.creditScore   = creditScore;
        this.autoPayEnabled = autoPayEnabled;
        this.startDate     = LocalDate.now();

        // Set interest rate based on credit score
        this.interestRate = determineInterestRate(creditScore, carCondition);

        // Calculate monthly payment
        this.monthlyPayment  = round(calcMonthlyPayment());
        this.remainingBalance = round(this.principal);

        this.totalPaid      = 0.0;
        this.paymentsMade   = 0;
        this.missedPayments = 0;
        this.delinquency    = "CURRENT";
        this.status         = "ACTIVE";
    }

    // Private constructor for loading from CSV
    private AutomobileLoan() {}

    // ----------------------------------------------------------
    // LOAN ID GENERATOR
    // ----------------------------------------------------------

    private String generateLoanID() {
        Random rand = new Random();
        return "AUTO-" + (10000 + rand.nextInt(90000));
    }

    // ----------------------------------------------------------
    // INTEREST RATE: based on credit score + car condition
    // Used cars get a slightly higher rate due to higher risk.
    // Follows PersonalLoan.java pattern.
    // ----------------------------------------------------------

    public static double determineInterestRate(int creditScore, String carCondition) {
        double baseRate;

        if (creditScore >= 750) {
            baseRate = 0.04;  // Excellent credit
        } else if (creditScore >= 700) {
            baseRate = 0.06;  // Good credit
        } else if (creditScore >= 650) {
            baseRate = 0.09;  // Fair credit
        } else if (creditScore >= 600) {
            baseRate = 0.13;  // Poor credit
        } else {
            baseRate = -1;    // Denied
        }

        // Used cars get +1.5% rate
        if (baseRate > 0 && carCondition.equalsIgnoreCase("used")) {
            baseRate += 0.015;
        }

        return baseRate;
    }

    // ----------------------------------------------------------
    // APPROVAL CHECK
    // ----------------------------------------------------------

    /**
     * Returns true if the loan is approved based on credit score
     * and debt-to-income ratio.
     *
     * DTI ratio = total monthly debt / monthly income
     * Standard approval threshold: DTI <= 43%
     */
    public static boolean isApproved(int creditScore, double monthlyIncome,
                                     double monthlyDebt, double monthlyPayment) {
        if (creditScore < 600) {
            System.out.println("Loan denied: Credit score too low (minimum 600).");
            return false;
        }

        double totalDebt = monthlyDebt + monthlyPayment;
        double dtiRatio  = (monthlyIncome > 0) ? totalDebt / monthlyIncome : 1.0;

        if (dtiRatio > 0.43) {
            System.out.printf("Loan denied: Debt-to-income ratio %.1f%% exceeds 43%% limit.%n",
                    dtiRatio * 100);
            return false;
        }

        return true;
    }

    // ----------------------------------------------------------
    // MONTHLY PAYMENT CALCULATION (amortization formula)
    // ----------------------------------------------------------

    private double calcMonthlyPayment() {
        if (principal <= 0) return 0;
        double r = interestRate / 12.0;
        if (r == 0) return round(principal / termMonths);
        return round((principal * r) / (1 - Math.pow(1 + r, -termMonths)));
    }

    // ----------------------------------------------------------
    // CHECK IF CUSTOMER ALREADY HAS A LOAN
    // ----------------------------------------------------------

    /**
     * Reads AutomobileLoan.csv and returns true if the customer
     * already has an active auto loan.
     */
    public static boolean hasExistingLoan(String customerID) {
        File file = new File(FILE);
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(",", -1);
                // customerID = col 1, status = col 21
                if (fields.length > 21 &&
                        fields[1].trim().equals(customerID) &&
                        fields[21].trim().equalsIgnoreCase("ACTIVE")) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not check existing loans: " + e.getMessage());
        }
        return false;
    }

    // ----------------------------------------------------------
    // MAKE A PAYMENT FROM CHECKING
    // ----------------------------------------------------------

    /**
     * Makes a monthly payment from the customer's checking account.
     * Follows PersonalLoan.java pattern.
     */
    public boolean payFromChecking(CheckingAccount.CheckingUser user, double amount) {
        if (!status.equals("ACTIVE")) {
            System.out.println("Loan is not active.");
            return false;
        }
        if (user == null || user.accounts.isEmpty()) {
            System.out.println("No checking account found.");
            return false;
        }
        if (amount <= 0) {
            System.out.println("Payment must be greater than $0.");
            return false;
        }

        CheckingAccount.Account acc = user.accounts.get(0);
        if (acc.balance < amount) {
            System.out.println("Insufficient funds in checking.");
            missedPayments++;
            updateDelinquency();
            return false;
        }

        user.withdraw(acc.accountID, amount);
        return applyPayment(amount);
    }

    // ----------------------------------------------------------
    // MAKE A PAYMENT FROM SAVINGS
    // ----------------------------------------------------------

    /**
     * Makes a monthly payment from the customer's savings account.
     */
    public boolean payFromSavings(CheckingAccount.SavingsAccount savings, double amount) throws IOException {
        if (!status.equals("ACTIVE")) {
            System.out.println("Loan is not active.");
            return false;
        }
        if (savings == null) {
            System.out.println("No savings account found.");
            return false;
        }
        if (amount <= 0) {
            System.out.println("Payment must be greater than $0.");
            return false;
        }
        if (savings.balance < amount) {
            System.out.println("Insufficient funds in savings.");
            missedPayments++;
            updateDelinquency();
            return false;
        }

        savings.balance -= amount;
        return applyPayment(amount);
    }

    // ----------------------------------------------------------
    // AUTO PAY
    // ----------------------------------------------------------

    /**
     * Automatically pays the monthly amount from checking.
     * Falls back to savings if checking has insufficient funds.
     */
    public boolean runAutoPay(CheckingAccount.CheckingUser user,
                              CheckingAccount.SavingsAccount savings) throws IOException {
        if (!autoPayEnabled) {
            System.out.println("Auto pay is disabled for this loan.");
            return false;
        }

        System.out.println("Running auto pay for loan " + loanID + "...");
        double due = getCurrentMonthlyDue();

        // Try checking first
        if (user != null && !user.accounts.isEmpty() &&
                user.accounts.get(0).balance >= due) {
            return payFromChecking(user, due);
        }

        // Fall back to savings
        if (savings != null && savings.balance >= due) {
            try {
                return payFromSavings(savings, due);
            } catch (IOException e) {
                System.out.println("Error processing savings payment: " + e.getMessage());
            }
        }

        System.out.println("Auto pay failed: insufficient funds in checking and savings.");
        missedPayments++;
        updateDelinquency();
        return false;
    }

    // ----------------------------------------------------------
    // APPLY PAYMENT (internal helper)
    // ----------------------------------------------------------

    private boolean applyPayment(double amount) {
        double applied    = round(Math.min(amount, remainingBalance));
        totalPaid         = round(totalPaid + applied);
        remainingBalance  = round(remainingBalance - applied);
        paymentsMade++;

        if (missedPayments > 0) missedPayments--;
        updateDelinquency();

        if (remainingBalance <= 0) {
            remainingBalance = 0;
            status           = "CLOSED";
            delinquency      = "PAID_OFF";
            System.out.println("Loan " + loanID + " is fully paid off!");
        }

        System.out.printf("Payment of $%.2f applied. Remaining balance: $%.2f%n",
                applied, remainingBalance);
        saveToCSV();
        return true;
    }

    // ----------------------------------------------------------
    // DELINQUENCY TRACKING (matches MortgageLoan pattern)
    // ----------------------------------------------------------

    private void updateDelinquency() {
        if (missedPayments >= DAYS_90)      delinquency = "90_DAYS_LATE";
        else if (missedPayments == DAYS_60) delinquency = "60_DAYS_LATE";
        else if (missedPayments == DAYS_30) delinquency = "30_DAYS_LATE";
        else                                delinquency = "CURRENT";
    }

    // ----------------------------------------------------------
    // DISPLAY
    // ----------------------------------------------------------

    public void display() {
        System.out.println("\n========================================");
        System.out.println("       AUTOMOBILE LOAN SUMMARY");
        System.out.println("========================================");
        System.out.println("Loan ID:          " + loanID);
        System.out.println("Customer:         " + customerName + " (" + customerID + ")");
        System.out.println("Status:           " + status);
        System.out.println("Delinquency:      " + delinquency);
        System.out.println("Start Date:       " + startDate);
        System.out.println("----------------------------------------");
        System.out.println("CAR INFORMATION:");
        System.out.println("  Make:           " + carMake);
        System.out.println("  Model:          " + carModel);
        System.out.println("  Year:           " + carYear);
        System.out.println("  Condition:      " + carCondition);
        System.out.println("----------------------------------------");
        System.out.println("FINANCIAL DETAILS:");
        System.out.printf( "  Vehicle Price:  $%,.2f%n", vehiclePrice);
        System.out.printf( "  NJ Sales Tax:   $%,.2f%n", salesTax);
        System.out.printf( "  Total Cost:     $%,.2f%n", totalVehicleCost);
        System.out.printf( "  Down Payment:   $%,.2f%n", downPayment);
        System.out.printf( "  Loan Principal: $%,.2f%n", principal);
        System.out.printf( "  Interest Rate:  %.2f%%%n", interestRate * 100);
        System.out.println("  Term:           " + termMonths + " months");
        System.out.printf( "  Monthly Due:    $%,.2f%n", monthlyPayment);
        System.out.println("----------------------------------------");
        System.out.println("PAYMENT STATUS:");
        System.out.printf( "  Remaining:      $%,.2f%n", remainingBalance);
        System.out.printf( "  Total Paid:     $%,.2f%n", totalPaid);
        System.out.println("  Payments Made:  " + paymentsMade);
        System.out.println("  Missed:         " + missedPayments);
        System.out.println("  Credit Score:   " + creditScore);
        System.out.println("  Auto Pay:       " + (autoPayEnabled ? "Enabled" : "Disabled"));
        System.out.println("========================================\n");
    }

    // ----------------------------------------------------------
    // SAVE TO CSV
    // ----------------------------------------------------------

    public void saveToCSV() {
        try {
            File file = new File(FILE);
            boolean fileExists = file.exists();

            // If file exists, update existing row; otherwise append
            if (fileExists) {
                updateCSV();
            } else {
                // Create new file with header
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write(HEADER);
                    bw.newLine();
                    bw.write(toCSVLine());
                    bw.newLine();
                }
                System.out.println("AutomobileLoan.csv created and loan saved.");
            }

        } catch (IOException e) {
            System.out.println("Warning: Could not save loan to CSV: " + e.getMessage());
        }
    }

    // Updates existing row in CSV if loan already exists, appends if not
    private void updateCSV() throws IOException {
        File file     = new File(FILE);
        File tempFile = new File("AutomobileLoan_temp.csv");
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    bw.write(line);
                    bw.newLine();
                    firstLine = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                String[] fields = line.split(",", -1);
                if (fields.length > 0 && fields[0].trim().equals(loanID)) {
                    bw.write(toCSVLine());
                    found = true;
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }

            // If not found, append as new row
            if (!found) {
                bw.write(toCSVLine());
                bw.newLine();
            }
        }

        // Replace original with updated temp file
        file.delete();
        tempFile.renameTo(file);
    }

    // Converts this loan to a CSV line
    private String toCSVLine() {
        return String.join(",",
                loanID, customerID, customerName,
                carMake, carModel,
                String.valueOf(carYear), carCondition,
                String.format("%.2f", vehiclePrice),
                String.format("%.2f", salesTax),
                String.format("%.2f", totalVehicleCost),
                String.format("%.2f", downPayment),
                String.format("%.2f", principal),
                String.format("%.4f", interestRate),
                String.valueOf(termMonths),
                String.format("%.2f", monthlyPayment),
                String.format("%.2f", remainingBalance),
                String.format("%.2f", totalPaid),
                String.valueOf(paymentsMade),
                String.valueOf(missedPayments),
                delinquency,
                status,
                String.valueOf(autoPayEnabled),
                String.valueOf(creditScore),
                startDate.toString()
        );
    }

    // ----------------------------------------------------------
    // LOAD ALL LOANS FOR A CUSTOMER FROM CSV
    // ----------------------------------------------------------

    public static List<AutomobileLoan> loadLoans(String customerID) {
        List<AutomobileLoan> loans = new ArrayList<>();
        File file = new File(FILE);
        if (!file.exists()) return loans;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                if (line.trim().isEmpty()) continue;

                AutomobileLoan loan = fromCSVLine(line);
                if (loan != null && loan.customerID.equals(customerID)) {
                    loans.add(loan);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not load loans: " + e.getMessage());
        }
        return loans;
    }

    // Parses one CSV line into an AutomobileLoan object
    private static AutomobileLoan fromCSVLine(String line) {
        String[] f = line.split(",", -1);
        if (f.length < 24) return null;

        try {
            AutomobileLoan loan     = new AutomobileLoan();
            loan.loanID             = f[0].trim();
            loan.customerID         = f[1].trim();
            loan.customerName       = f[2].trim();
            loan.carMake            = f[3].trim();
            loan.carModel           = f[4].trim();
            loan.carYear            = Integer.parseInt(f[5].trim());
            loan.carCondition       = f[6].trim();
            loan.vehiclePrice       = Double.parseDouble(f[7].trim());
            loan.salesTax           = Double.parseDouble(f[8].trim());
            loan.totalVehicleCost   = Double.parseDouble(f[9].trim());
            loan.downPayment        = Double.parseDouble(f[10].trim());
            loan.principal          = Double.parseDouble(f[11].trim());
            loan.interestRate       = Double.parseDouble(f[12].trim());
            loan.termMonths         = Integer.parseInt(f[13].trim());
            loan.monthlyPayment     = Double.parseDouble(f[14].trim());
            loan.remainingBalance   = Double.parseDouble(f[15].trim());
            loan.totalPaid          = Double.parseDouble(f[16].trim());
            loan.paymentsMade       = Integer.parseInt(f[17].trim());
            loan.missedPayments     = Integer.parseInt(f[18].trim());
            loan.delinquency        = f[19].trim();
            loan.status             = f[20].trim();
            loan.autoPayEnabled     = Boolean.parseBoolean(f[21].trim());
            loan.creditScore        = Integer.parseInt(f[22].trim());
            loan.startDate          = LocalDate.parse(f[23].trim());
            return loan;
        } catch (Exception e) {
            System.out.println("Warning: Could not parse loan row: " + e.getMessage());
            return null;
        }
    }

    // ----------------------------------------------------------
    // GETTERS
    // ----------------------------------------------------------

    public String getLoanID()           { return loanID; }
    public String getCustomerID()       { return customerID; }
    public String getCustomerName()     { return customerName; }
    public String getCarMake()          { return carMake; }
    public String getCarModel()         { return carModel; }
    public int    getCarYear()          { return carYear; }
    public String getCarCondition()     { return carCondition; }
    public double getVehiclePrice()     { return vehiclePrice; }
    public double getSalesTax()         { return salesTax; }
    public double getTotalVehicleCost() { return totalVehicleCost; }
    public double getDownPayment()      { return downPayment; }
    public double getPrincipal()        { return principal; }
    public double getInterestRate()     { return interestRate; }
    public int    getTermMonths()       { return termMonths; }
    public double getMonthlyPayment()   { return monthlyPayment; }
    public double getRemainingBalance() { return remainingBalance; }
    public double getTotalPaid()        { return totalPaid; }
    public int    getPaymentsMade()     { return paymentsMade; }
    public int    getMissedPayments()   { return missedPayments; }
    public String getDelinquency()      { return delinquency; }
    public String getStatus()           { return status; }
    public boolean isAutoPayEnabled()   { return autoPayEnabled; }
    public int    getCreditScore()      { return creditScore; }
    public LocalDate getStartDate()     { return startDate; }

    public double getCurrentMonthlyDue() {
        if (!status.equals("ACTIVE")) return 0;
        return round(Math.min(monthlyPayment, remainingBalance));
    }

    public boolean isPaidOff() { return remainingBalance <= 0; }

    // ----------------------------------------------------------
    // HELPER: round to 2 decimal places
    // ----------------------------------------------------------
    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // ----------------------------------------------------------
    // INTERACTIVE MENU (matches MortgageLoan.launch() pattern)
    // ----------------------------------------------------------

    /**
     * Launches the automobile loan menu for a customer.
     * Call this from your main menu (Mario's menu.java).
     */
    public static void launch(Scanner sc, User user,
                              List<CheckingAccount.CheckingUser> checkingUsers) throws IOException {

        String customerID   = user.customerID;
        String customerName = user.firstName + " " + user.lastName;

        // Load savings
        CheckingAccount.SavingsAccount savings = null;
        // Would need to load from CSV if necessary

        // Load checking
        CheckingAccount.CheckingUser checkUser = CheckingAccount.findUser(checkingUsers, customerID);

        // Load existing loans
        List<AutomobileLoan> loans = loadLoans(customerID);

        while (true) {
            System.out.println("\n--- AUTOMOBILE LOAN ---");
            System.out.println("1. View my loans");
            System.out.println("2. Apply for a new loan");
            System.out.println("3. Make a payment");
            System.out.println("4. Run auto pay");
            System.out.println("0. Back to main menu");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {

                case "1" -> {
                    if (loans.isEmpty()) {
                        System.out.println("No automobile loans found.");
                    } else {
                        for (AutomobileLoan loan : loans) loan.display();
                    }
                }

                case "2" -> {
                    // Check for existing active loan
                    if (hasExistingLoan(customerID)) {
                        System.out.println("You already have an active automobile loan.");
                        System.out.println("Please pay off your existing loan before applying for a new one.");
                        break;
                    }

                    // Collect car information
                    System.out.println("\n--- CAR INFORMATION ---");
                    System.out.print("Car Make (e.g. Toyota): ");
                    String make = sc.nextLine().trim();

                    System.out.print("Car Model (e.g. Camry): ");
                    String model = sc.nextLine().trim();

                    System.out.print("Car Year (e.g. 2022): ");
                    int year = 0;
                    try { year = Integer.parseInt(sc.nextLine().trim()); }
                    catch (NumberFormatException e) {
                        System.out.println("Invalid year. Defaulting to 2020.");
                        year = 2020;
                    }

                    System.out.print("Condition (New/Used): ");
                    String condition = sc.nextLine().trim();
                    if (!condition.equalsIgnoreCase("new") &&
                            !condition.equalsIgnoreCase("used")) {
                        System.out.println("Invalid condition. Defaulting to Used.");
                        condition = "Used";
                    }

                    System.out.print("Vehicle Price: $");
                    double price = 0;
                    try { price = Double.parseDouble(sc.nextLine().trim()); }
                    catch (NumberFormatException e) {
                        System.out.println("Invalid price.");
                        break;
                    }

                    System.out.print("Down Payment: $");
                    double downPay = 0;
                    try { downPay = Double.parseDouble(sc.nextLine().trim()); }
                    catch (NumberFormatException e) {
                        System.out.println("Invalid down payment.");
                        break;
                    }

                    System.out.print("Loan Term in months (e.g. 60): ");
                    int term = 60;
                    try { term = Integer.parseInt(sc.nextLine().trim()); }
                    catch (NumberFormatException e) {
                        System.out.println("Invalid term. Defaulting to 60 months.");
                    }

                    // Collect financial survey info
                    System.out.println("\n--- FINANCIAL SURVEY ---");
                    System.out.print("Monthly gross income: $");
                    double income = 0;
                    try { income = Double.parseDouble(sc.nextLine().trim()); }
                    catch (NumberFormatException e) {
                        System.out.println("Invalid income.");
                        break;
                    }

                    System.out.print("Total monthly debt (excluding this loan): $");
                    double debt = 0;
                    try { debt = Double.parseDouble(sc.nextLine().trim()); }
                    catch (NumberFormatException e) {
                        System.out.println("Invalid debt amount.");
                        break;
                    }

                    // Get credit score from user's credit card
                    int creditScore = user.creditScore;

                    System.out.print("Enable auto pay? (yes/no): ");
                    boolean autoPay = sc.nextLine().trim().equalsIgnoreCase("yes");

                    // Determine interest rate
                    double rate = determineInterestRate(creditScore, condition);
                    if (rate < 0) {
                        System.out.println("Loan denied: Credit score too low (minimum 600 required).");
                        break;
                    }

                    // Calculate estimated monthly payment for DTI check
                    double tempPrincipal = round(price + (price * NJ_SALES_TAX) - downPay);
                    double r = rate / 12.0;
                    double estMonthly = (r == 0) ? round(tempPrincipal / term) :
                            round((tempPrincipal * r) / (1 - Math.pow(1 + r, -term)));

                    // Check approval
                    if (!isApproved(creditScore, income, debt, estMonthly)) {
                        break;
                    }

                    // Create loan
                    AutomobileLoan newLoan = new AutomobileLoan(
                            customerID, customerName,
                            make, model, year, condition,
                            price, downPay, term,
                            creditScore, autoPay);

                    newLoan.saveToCSV();
                    loans.add(newLoan);

                    System.out.println("\nLoan approved!");
                    newLoan.display();
                }

                case "3" -> {
                    if (loans.isEmpty()) {
                        System.out.println("No active loans found.");
                        break;
                    }

                    // Pick a loan
                    System.out.println("Select a loan:");
                    for (int i = 0; i < loans.size(); i++) {
                        System.out.println((i + 1) + ". " + loans.get(i).getLoanID() +
                                " - $" + String.format("%.2f", loans.get(i).getRemainingBalance()) +
                                " remaining");
                    }
                    System.out.print("Enter number: ");
                    int idx = -1;
                    try { idx = Integer.parseInt(sc.nextLine().trim()) - 1; }
                    catch (NumberFormatException e) { break; }
                    if (idx < 0 || idx >= loans.size()) { System.out.println("Invalid choice."); break; }

                    AutomobileLoan selected = loans.get(idx);

                    System.out.printf("Monthly payment due: $%.2f%n", selected.getCurrentMonthlyDue());
                    System.out.println("Pay from:");
                    System.out.println("1. Checking");
                    System.out.println("2. Savings");
                    System.out.print("Choice: ");
                    String payChoice = sc.nextLine().trim();

                    if (payChoice.equals("1")) {
                        selected.payFromChecking(checkUser, selected.getCurrentMonthlyDue());
                        if (checkUser != null) {
                            CheckingAccount.writeCSV("checking_accounts.csv", checkingUsers);
                        }
                    } else if (payChoice.equals("2")) {
                        selected.payFromSavings(savings, selected.getCurrentMonthlyDue());
                    } else {
                        System.out.println("Invalid choice.");
                    }
                }

                case "4" -> {
                    for (AutomobileLoan loan : loans) {
                        loan.runAutoPay(checkUser, savings);
                    }
                    if (checkUser != null) {
                        CheckingAccount.writeCSV("checking_accounts.csv", checkingUsers);
                    }
                }

                case "0" -> { return; }

                default -> System.out.println("Invalid choice. Please enter 0-4.");
            }
        }
    }
}