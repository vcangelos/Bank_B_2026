import java.util.ArrayList;
import java.util.List;
 
/**
 * Driver class demonstrating EmployeeAccount creation and usage.
 *
 * FIXES:
 * - Changed placeholder ID "223111" to "323111" (Alice Smith — exists in employees.csv)
 * - Added Bobby Johnson (395195) as a second example
 * - Added savings account demonstration
 */
public class EmployeeAccountDriver {
 
    public static void main(String[] args) {
 
        // Shared list of banking users passed to every EmployeeAccount
        List<BankingCSV.User> users = new ArrayList<>();
 
        // ---------------------------------------------------------
        // Employee 1: Alice Smith (ID 323111)
        // ---------------------------------------------------------
 
        System.out.println("\n>>> Creating account for Alice Smith...\n");
        EmployeeAccount emp1 = new EmployeeAccount("323111", "Alice Smith", users);
        emp1.showAccounts();
 
        // Deposit into Alice's checking account
        if (!emp1.getBankingUser().accounts.isEmpty()) {
            String aliceAccountID = emp1.getBankingUser().accounts.get(0).accountID;
            emp1.getBankingUser().deposit(aliceAccountID, 500.0);
            System.out.println("Deposited $500.00 into Alice's checking account.");
        }
 
        // Debit card operations
        System.out.println("\n>>> Debit card operations for Alice:");
        emp1.getDebitCard().deposit(200.0);
        emp1.getDebitCard().withdraw(50.0);
 
        // Credit card operations
        System.out.println("\n>>> Credit card operations for Alice:");
        emp1.getCreditCard().addCharge(100.0);
        emp1.getCreditCard().makePayment(25.0);
 
        // Savings account operations
        System.out.println("\n>>> Savings account operations for Alice:");
        if (emp1.getSavingsAccount() != null) {
            emp1.getSavingsAccount().depositSavings(50.0);
            System.out.printf("Savings balance after deposit: $%.2f%n",
                    emp1.getSavingsAccount().getSavings());
        }
 
        // ---------------------------------------------------------
        // Employee 2: Bobby Johnson (ID 395195)
        // ---------------------------------------------------------
 
        System.out.println("\n>>> Creating account for Bobby Johnson...\n");
        EmployeeAccount emp2 = new EmployeeAccount("395195", "Bobby Johnson", users);
        emp2.showAccounts();
    }
}
