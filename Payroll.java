// Payroll.java

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;

/**
 * Payroll.java
 *
 * Handles bi-monthly payroll for bank employees across all 5 branches.
 *
 * Features:
 * - Logs hours worked for the two-week period (salaried, hours for records only)
 * - Calculates gross pay from annual salary (salary / 24 pay periods)
 * - Asks user which branch they work at, applies correct state taxes
 * - Deducts all applicable taxes:
 *     Federal Income Tax  : 22%
 *     Medicare Tax        : 1.45%
 *     Social Security Tax : 6.2%
 *     FUTA Tax            : 6% on first $7,000/year (employer-side, logged)
 *     SUI Tax             : varies by state
 *     State Tax           : varies by state
 * - Lets employee split net pay between checking and savings
 * - Deposits into checking via BankingCSV, savings via SavingsAccount
 * - Logs every payroll run to payroll.csv
 *
 * Pay dates: 1st and 15th of every month.
 * To change pay dates, update PAY_DAY_1 and PAY_DAY_2 below.
 */
public class Payroll {

    // ----------------------------------------------------------
    // PAY DATE CONSTANTS
    // Ask your teacher if these should be different dates.
    // ----------------------------------------------------------
    public static final int PAY_DAY_1 = 1;   // 1st of the month
    public static final int PAY_DAY_2 = 15;  // 15th of the month

    // ----------------------------------------------------------
    // FEDERAL TAX CONSTANTS (same for all branches)
    // ----------------------------------------------------------
    public static final double FEDERAL_TAX_RATE     = 0.22;
    public static final double MEDICARE_RATE        = 0.0145;
    public static final double SOCIAL_SECURITY_RATE = 0.062;
    public static final double FUTA_RATE            = 0.06;
    public static final double FUTA_WAGE_BASE       = 7000.00;

    // ----------------------------------------------------------
    // PAY PERIODS per year (bi-monthly = 24)
    // ----------------------------------------------------------
    public static final int PAY_PERIODS = 24;

    // ----------------------------------------------------------
    // CSV paths
    // ----------------------------------------------------------
    private static final Path PAYROLL_CSV   = Path.of("payroll.csv");
    private static final Path EMPLOYEES_CSV = Path.of("employees.csv");

    private static final String PAYROLL_HEADER =
        "employeeID,name,branch,payDate,hoursWorked,grossPay," +
        "federalTax,medicareTax,socialSecurityTax,futaTax,suiTax,stateTax," +
        "totalDeductions,netPay,checkingDeposit,savingsDeposit,ytdGross";

    // ----------------------------------------------------------
    // Inner class: Branch
    // Holds branch-specific info and tax rates.
    // ----------------------------------------------------------
    public static class Branch {
        public String name;
        public String zipCode;
        public String routingNumber;
        public String state;
        public double stateTaxRate;
        public double suiRate;

        public Branch(String name, String zipCode, String routingNumber,
                      String state, double stateTaxRate, double suiRate) {
            this.name          = name;
            this.zipCode       = zipCode;
            this.routingNumber = routingNumber;
            this.state         = state;
            this.stateTaxRate  = stateTaxRate;
            this.suiRate       = suiRate;
        }

        public String toString() {
            return name + " (" + zipCode + ") | Routing: " + routingNumber +
                   " | State Tax: " + (stateTaxRate * 100) + "%" +
                   " | SUI: " + (suiRate * 100) + "%";
        }
    }

    // ----------------------------------------------------------
    // All 5 bank branches with their state tax rates
    // ----------------------------------------------------------
    public static final Branch[] BRANCHES = {
        new Branch("Bank of Old Bridge", "08857", "832954724", "New Jersey", 0.035,  0.00425),
        new Branch("Bank of Dallas",     "75001", "723297259", "Texas",      0.00,   0.0027),
        new Branch("Bank of Los Angeles","90008", "563934953", "California", 0.093,  0.009),
        new Branch("Bank of Detroit",    "48208", "384239475", "Michigan",   0.0425, 0.0006),
        new Branch("Bank of Denver",     "80014", "274539242", "Colorado",   0.044,  0.0017)
    };

    // ----------------------------------------------------------
    // Inner class: PayrollResult
    // Holds all calculated values for one pay run.
    // ----------------------------------------------------------
    public static class PayrollResult {
        public String employeeID;
        public String name;
        public Branch branch;
        public LocalDate payDate;
        public double hoursWorked;
        public double grossPay;
        public double federalTax;
        public double medicareTax;
        public double socialSecurityTax;
        public double futaTax;
        public double suiTax;
        public double stateTax;
        public double totalDeductions;
        public double netPay;
        public double checkingDeposit;
        public double savingsDeposit;
        public double ytdGross;

        // Prints a formatted pay stub to the console
        public void printPayStub() {
            System.out.println("\n========================================");
            System.out.println("           EMPLOYEE PAY STUB");
            System.out.println("========================================");
            System.out.println("Employee:           " + name);
            System.out.println("Employee ID:        " + employeeID);
            System.out.println("Branch:             " + branch.name);
            System.out.println("State:              " + branch.state);
            System.out.println("Pay Date:           " + payDate);
            System.out.printf( "Hours Logged:       %.1f hrs%n", hoursWorked);
            System.out.println("----------------------------------------");
            System.out.printf( "Gross Pay:                   $%9.2f%n", grossPay);
            System.out.println("----------------------------------------");
            System.out.println("DEDUCTIONS:");
            System.out.printf( "  Federal Tax      (22%%):    $%9.2f%n", federalTax);
            System.out.printf( "  Medicare         (1.45%%):  $%9.2f%n", medicareTax);
            System.out.printf( "  Social Security  (6.2%%):   $%9.2f%n", socialSecurityTax);
            System.out.printf( "  FUTA             (6%% cap):  $%9.2f%n", futaTax);
            System.out.printf( "  %-6s SUI  (%.3f%%):    $%9.2f%n",
                    branch.state, branch.suiRate * 100, suiTax);
            System.out.printf( "  %-6s State(%.2f%%):    $%9.2f%n",
                    branch.state, branch.stateTaxRate * 100, stateTax);
            System.out.println("  * FUTA applies to first $7,000/year only");
            System.out.println("----------------------------------------");
            System.out.printf( "Total Deductions:            $%9.2f%n", totalDeductions);
            System.out.printf( "NET PAY:                     $%9.2f%n", netPay);
            System.out.println("----------------------------------------");
            System.out.printf( "  Deposited to Checking:     $%9.2f%n", checkingDeposit);
            System.out.printf( "  Deposited to Savings:      $%9.2f%n", savingsDeposit);
            System.out.println("----------------------------------------");
            System.out.printf( "YTD Gross Pay:               $%9.2f%n", ytdGross);
            System.out.println("========================================\n");
        }
    }

    // ----------------------------------------------------------
    // isPayDay
    // ----------------------------------------------------------

    /**
     * Returns true if today is one of the two monthly pay days.
     */
    public static boolean isPayDay() {
        int today = LocalDate.now().getDayOfMonth();
        return today == PAY_DAY_1 || today == PAY_DAY_2;
    }

    // ----------------------------------------------------------
    // selectBranch
    // ----------------------------------------------------------

    /**
     * Displays all branches and lets the user pick one.
     */
    public static Branch selectBranch(Scanner scanner) {
        System.out.println("\n--- Select Your Branch ---");
        for (int i = 0; i < BRANCHES.length; i++) {
            System.out.println((i + 1) + ". " + BRANCHES[i].toString());
        }
        System.out.print("Enter branch number (1-" + BRANCHES.length + "): ");

        int choice = -1;
        while (choice < 1 || choice > BRANCHES.length) {
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice < 1 || choice > BRANCHES.length) {
                    System.out.print("Invalid. Enter a number 1-" + BRANCHES.length + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a number 1-" + BRANCHES.length + ": ");
            }
        }

        System.out.println("Branch selected: " + BRANCHES[choice - 1].name);
        return BRANCHES[choice - 1];
    }

    // ----------------------------------------------------------
    // getHoursWorked
    // ----------------------------------------------------------

    /**
     * Asks the employee how many hours they worked this pay period.
     * Hours are logged for records only (salaried employees).
     */
    public static double getHoursWorked(Scanner scanner) {
        System.out.print("Enter hours worked this pay period (standard is 80 hrs for 2 weeks): ");
        double hours = -1;
        while (hours < 0) {
            try {
                hours = Double.parseDouble(scanner.nextLine().trim());
                if (hours < 0) {
                    System.out.print("Hours cannot be negative. Try again: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a number: ");
                hours = -1;
            }
        }
        return hours;
    }

    // ----------------------------------------------------------
    // calculateGrossPay
    // ----------------------------------------------------------

    /**
     * Gross pay for one pay period = annual salary / 24.
     */
    public static double calculateGrossPay(int annualSalary) {
        return round(annualSalary / (double) PAY_PERIODS);
    }

    // ----------------------------------------------------------
    // getYTDGross
    // ----------------------------------------------------------

    /**
     * Reads payroll.csv and returns total gross pay earned by this
     * employee so far this calendar year, before the current run.
     */
    public static double getYTDGross(String employeeID) {
        if (!Files.exists(PAYROLL_CSV)) return 0.0;

        double ytd = 0.0;
        int currentYear = LocalDate.now().getYear();

        try (BufferedReader br = Files.newBufferedReader(PAYROLL_CSV)) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(",", -1);
                if (fields.length < 6) continue;

                String id      = fields[0].trim(); // employeeID col 0
                String dateStr = fields[3].trim(); // payDate col 3
                String gross   = fields[5].trim(); // grossPay col 5

                if (!id.equals(employeeID)) continue;
                try {
                    LocalDate date = LocalDate.parse(dateStr);
                    if (date.getYear() == currentYear) {
                        ytd += Double.parseDouble(gross);
                    }
                } catch (Exception e) {
                    // skip malformed rows
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: Could not read payroll.csv: " + e.getMessage());
        }

        return round(ytd);
    }

    // ----------------------------------------------------------
    // calculateTaxes
    // ----------------------------------------------------------

    /**
     * Calculates all tax deductions and populates the PayrollResult.
     * FUTA is capped at the $7,000 annual wage base.
     */
    public static void calculateTaxes(double grossPay, double ytdGross,
                                      Branch branch, PayrollResult result) {
        // Federal
        result.federalTax        = round(grossPay * FEDERAL_TAX_RATE);
        result.medicareTax       = round(grossPay * MEDICARE_RATE);
        result.socialSecurityTax = round(grossPay * SOCIAL_SECURITY_RATE);

        // FUTA: only on wages up to $7,000/year
        double futaEligible = Math.max(0,
                Math.min(grossPay, FUTA_WAGE_BASE - ytdGross));
        result.futaTax = round(futaEligible * FUTA_RATE);

        // State-specific
        result.suiTax   = round(grossPay * branch.suiRate);
        result.stateTax = round(grossPay * branch.stateTaxRate);

        // Totals
        result.totalDeductions = round(
                result.federalTax + result.medicareTax +
                result.socialSecurityTax + result.futaTax +
                result.suiTax + result.stateTax);
        result.netPay = round(grossPay - result.totalDeductions);
    }

    // ----------------------------------------------------------
    // getSplitAmounts
    // ----------------------------------------------------------

    /**
     * Asks the employee how to split net pay between checking and savings.
     */
    public static void getSplitAmounts(Scanner scanner, double netPay,
                                       PayrollResult result) {
        System.out.printf("%nYour net pay is $%.2f%n", netPay);
        System.out.println("How would you like to receive your pay?");
        System.out.println("1. All to checking");
        System.out.println("2. Split between checking and savings");
        System.out.print("Enter choice (1 or 2): ");

        String choice = scanner.nextLine().trim();

        if (choice.equals("2")) {
            double savingsAmount = -1;
            while (savingsAmount < 0 || savingsAmount > netPay) {
                System.out.printf(
                        "Enter amount to deposit into savings (max $%.2f): $", netPay);
                try {
                    savingsAmount = Double.parseDouble(scanner.nextLine().trim());
                    if (savingsAmount < 0 || savingsAmount > netPay) {
                        System.out.printf(
                                "Must be between $0.00 and $%.2f. Try again.%n", netPay);
                        savingsAmount = -1;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Enter a number.");
                    savingsAmount = -1;
                }
            }
            result.savingsDeposit  = round(savingsAmount);
            result.checkingDeposit = round(netPay - savingsAmount);
        } else {
            result.checkingDeposit = netPay;
            result.savingsDeposit  = 0.0;
        }
    }

    // ----------------------------------------------------------
    // depositPay
    // ----------------------------------------------------------

    /**
     * Deposits net pay into the employee's checking and/or savings accounts.
     */
    public static void depositPay(PayrollResult result,
                                  List<BankingCSV.User> bankingUsers) {
        // Checking deposit via BankingCSV
        if (result.checkingDeposit > 0) {
            BankingCSV.User user = BankingCSV.findUser(bankingUsers, result.employeeID);
            if (user != null && !user.accounts.isEmpty()) {
                String accountID = user.accounts.get(0).accountID;
                user.deposit(accountID, result.checkingDeposit);
                try {
                    BankingCSV.writeCSV("checking_accounts.csv", bankingUsers);
                } catch (IOException e) {
                    System.out.println("Warning: Could not update checking_accounts.csv: "
                            + e.getMessage());
                }
            } else {
                System.out.println("Warning: No checking account found for " +
                        result.employeeID + ". Checking deposit skipped.");
            }
        }

        // Savings deposit via SavingsAccount
        if (result.savingsDeposit > 0) {
            try {
                if (SavingsAccount.userIDExists(result.employeeID)) {
                    SavingsAccount savings =
                            SavingsAccount.OpenSavingsAccount(result.employeeID);
                    if (savings != null) {
                        savings.depositSavings(result.savingsDeposit);
                        SavingsAccount.writeSavingsCSV(
                                result.employeeID,
                                savings.getSavingsID(),
                                savings.getSavings());
                        System.out.printf("Deposited $%.2f into savings.%n",
                                result.savingsDeposit);
                    }
                } else {
                    System.out.println("Warning: No savings account found for " +
                            result.employeeID + ". Savings deposit skipped.");
                }
            } catch (IOException e) {
                System.out.println("Warning: Could not update savings: " + e.getMessage());
            }
        }
    }

    // ----------------------------------------------------------
    // saveToCSV
    // ----------------------------------------------------------

    /**
     * Appends one payroll record to payroll.csv.
     * Creates the file with headers if it doesn't exist.
     */
    public static void saveToCSV(PayrollResult result) {
        try {
            if (!Files.exists(PAYROLL_CSV)) {
                try (BufferedWriter bw = Files.newBufferedWriter(
                        PAYROLL_CSV, StandardOpenOption.CREATE)) {
                    bw.write(PAYROLL_HEADER);
                    bw.newLine();
                }
            }

            try (BufferedWriter bw = Files.newBufferedWriter(
                    PAYROLL_CSV, StandardOpenOption.APPEND)) {
                bw.write(String.join(",",
                        result.employeeID,
                        result.name,
                        result.branch.name,
                        result.payDate.toString(),
                        String.format("%.1f",  result.hoursWorked),
                        String.format("%.2f",  result.grossPay),
                        String.format("%.2f",  result.federalTax),
                        String.format("%.2f",  result.medicareTax),
                        String.format("%.2f",  result.socialSecurityTax),
                        String.format("%.2f",  result.futaTax),
                        String.format("%.2f",  result.suiTax),
                        String.format("%.2f",  result.stateTax),
                        String.format("%.2f",  result.totalDeductions),
                        String.format("%.2f",  result.netPay),
                        String.format("%.2f",  result.checkingDeposit),
                        String.format("%.2f",  result.savingsDeposit),
                        String.format("%.2f",  result.ytdGross)
                ));
                bw.newLine();
            }

            System.out.println("Payroll record saved to payroll.csv.");

        } catch (IOException e) {
            System.out.println("Warning: Could not save to payroll.csv: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------
    // getEmployeeSalary: reads from employees.csv
    // ----------------------------------------------------------

    /**
     * Looks up an employee's annual salary from employees.csv.
     * Returns -1 if not found.
     */
    public static int getEmployeeSalary(String employeeID) {
        if (!Files.exists(EMPLOYEES_CSV)) return -1;

        try (BufferedReader br = Files.newBufferedReader(EMPLOYEES_CSV)) {
            String headerLine = br.readLine();
            if (headerLine == null) return -1;

            String[] headers  = headerLine.split(",", -1);
            int idIdx     = -1;
            int salaryIdx = -1;

            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equals("employee_id")) idIdx     = i;
                if (headers[i].trim().equals("salary"))      salaryIdx = i;
            }

            if (idIdx == -1 || salaryIdx == -1) return -1;

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(",", -1);
                if (fields.length > idIdx &&
                        fields[idIdx].trim().equals(employeeID)) {
                    return Integer.parseInt(fields[salaryIdx].trim());
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Warning: Could not read salary: " + e.getMessage());
        }
        return -1;
    }

    // ----------------------------------------------------------
    // runPayroll: ties everything together
    // ----------------------------------------------------------

    /**
     * Runs the full payroll process for one employee.
     *
     * @param scanner       Scanner for user input
     * @param employeeID    Employee's ID string
     * @param name          Employee's full name
     * @param bankingUsers  Shared BankingCSV user list
     * @param forceRun      If true, skips the pay day check (useful for testing)
     */
    public static void runPayroll(Scanner scanner, String employeeID,
                                  String name, List<BankingCSV.User> bankingUsers,
                                  boolean forceRun) {

        System.out.println("\n========== PAYROLL SYSTEM ==========");
        System.out.println("Employee: " + name + " | ID: " + employeeID);

        // Step 1: Pay day check
        if (!forceRun && !isPayDay()) {
            System.out.println("Today (" + LocalDate.now() + ") is not a pay day.");
            System.out.println("Scheduled pay days: " + PAY_DAY_1 +
                    "st and " + PAY_DAY_2 + "th of each month.");
            System.out.println("Payroll not processed.");
            return;
        }

        // Step 2: Get salary from employees.csv
        int annualSalary = getEmployeeSalary(employeeID);
        if (annualSalary == -1) {
            System.out.println("Error: Salary not found for ID " +
                    employeeID + ". Payroll cancelled.");
            return;
        }
        System.out.println("Annual Salary: $" + annualSalary);

        // Step 3: Select branch
        Branch branch = selectBranch(scanner);

        // Step 4: Log hours worked
        double hoursWorked = getHoursWorked(scanner);

        // Step 5: Calculate pay and taxes
        PayrollResult result  = new PayrollResult();
        result.employeeID     = employeeID;
        result.name           = name;
        result.branch         = branch;
        result.payDate        = LocalDate.now();
        result.hoursWorked    = hoursWorked;
        result.grossPay       = calculateGrossPay(annualSalary);
        double ytdBeforeThis  = getYTDGross(employeeID);

        calculateTaxes(result.grossPay, ytdBeforeThis, branch, result);

        // YTD including this period
        result.ytdGross = round(ytdBeforeThis + result.grossPay);

        // Step 6: Split net pay
        getSplitAmounts(scanner, result.netPay, result);

        // Step 7: Deposit
        depositPay(result, bankingUsers);

        // Step 8: Print pay stub
        result.printPayStub();

        // Step 9: Save to payroll.csv
        saveToCSV(result);
    }

    // ----------------------------------------------------------
    // Helper: round to 2 decimal places
    // ----------------------------------------------------------
    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
