// PayrollDriver.java

import java.util.*;

/**
 * PayrollDriver.java
 *
 * Tests the Payroll system for bank employees.
 *
 * Loads existing banking users from checking_accounts.csv,
 * then runs payroll for a selected employee.
 *
 * Note: forceRun = true so payroll runs regardless of today's date.
 * Set forceRun = false in production so it only runs on pay days.
 */
public class PayrollDriver {

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        // Load existing banking users from checking_accounts.csv
        List<BankingCSV.User> bankingUsers = new ArrayList<>();
        try {
            bankingUsers = BankingCSV.readCSV("checking_accounts.csv");
            System.out.println("Loaded " + bankingUsers.size() +
                    " user(s) from checking_accounts.csv");
        } catch (Exception e) {
            System.out.println("Note: checking_accounts.csv not found. " +
                    "Checking deposits will be skipped.");
        }

        // Show pay day info
        System.out.println("\n--- Payroll System ---");
        System.out.println("Scheduled pay days: " +
                Payroll.PAY_DAY_1 + "st and " +
                Payroll.PAY_DAY_2 + "th of each month.");
        System.out.println("Today is: " + java.time.LocalDate.now());

        // Ask which employee to run payroll for
        System.out.println("\nWhich employee would you like to run payroll for?");
        System.out.println("1. Alice Smith   (ID: 323111)");
        System.out.println("2. Bobby Johnson (ID: 395195)");
        System.out.println("3. Enter manually");
        System.out.print("Enter choice: ");

        String choice = scanner.nextLine().trim();
        String employeeID;
        String employeeName;

        switch (choice) {
            case "1":
                employeeID   = "323111";
                employeeName = "Alice Smith";
                break;
            case "2":
                employeeID   = "395195";
                employeeName = "Bobby Johnson";
                break;
            default:
                System.out.print("Enter employee ID: ");
                employeeID = scanner.nextLine().trim();
                System.out.print("Enter employee name: ");
                employeeName = scanner.nextLine().trim();
                break;
        }

        // Run payroll
        // forceRun = true  → skips pay day check (for testing)
        // forceRun = false → only runs on 1st and 15th (production)
        Payroll.runPayroll(scanner, employeeID, employeeName, bankingUsers, true);

        scanner.close();
    }
}
