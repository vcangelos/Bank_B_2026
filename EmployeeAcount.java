// Import List so we can store multiple banking users
// List is part of java.util and represents a dynamic collection of objects
import java.util.List;

// This class represents an employee and links them to their banking systems
public class EmployeeAccount {

    // Stores the employee's ID
    // We use String instead of int because IDs may contain leading zeros
    private String employeeID;

    // Stores the employee's name
    private String name;

    // -----------------------------
    // Linked systems
    // -----------------------------

    // This represents the banking user stored in the BankingCSV system
    // BankingCSV.User is likely an inner class inside BankingCSV
    private BankingCSV.User bankingUser;

    // Each employee has a debit card connected to one of their bank accounts
    private DebitCard debitCard;

    // Each employee may also have a credit card
    private CreditCard creditCard;


    // -----------------------------
    // Constructor
    // -----------------------------
    // A constructor initializes a new EmployeeAccount object

    public EmployeeAccount(String employeeID, String name, List<BankingCSV.User> existingUsers) {

        // Save employee information
        this.employeeID = employeeID;
        this.name = name;

        // -----------------------------
        // LINK BANKING USER
        // -----------------------------

        // Try to find an existing banking user that matches this employee ID
        // This prevents creating duplicate banking accounts
        this.bankingUser = BankingCSV.findUser(existingUsers, employeeID);

        // If no user exists with this employeeID
        if (this.bankingUser == null) {

            // Create a new banking user
            // This connects the employee to the banking system
            this.bankingUser = new BankingCSV.User(employeeID, name);

            // Add the new user to the list of banking users
            existingUsers.add(this.bankingUser);
        }


        // -----------------------------
        // LINK DEBIT CARD
        // -----------------------------

        // Create a debit card for this employee

        this.debitCard = new DebitCard(

                // Generate a random card number
                DebitCard.generateCardNumber(),

                // Default PIN (for testing/demo purposes)
                "1234",

                // Link the debit card to the employee ID
                employeeID,

                // Determine which bank account the card is attached to
                // If the employee has no accounts yet, assign a placeholder
                this.bankingUser.accounts.isEmpty()

                        ? "NEW_ACCOUNT" // if no accounts exist

                        : this.bankingUser.accounts.get(0).accountID // otherwise use the first account
        );

        // Provide the DebitCard class access to the banking users list
        // This allows debit cards to locate accounts and update balances
        DebitCard.setBankingUsers(existingUsers);


        // -----------------------------
        // LINK CREDIT CARD
        // -----------------------------

        // Create a credit card for the employee
        // Credit cards are independent from checking/savings accounts
        this.creditCard = new CreditCard();
    }


    // -----------------------------
    // Accessor Methods (Getters)
    // -----------------------------

    // Getter for employee ID
    public String getEmployeeID() {
        return employeeID;
    }

    // Getter for employee name
    public String getName() {
        return name;
    }

    // Returns the linked banking user object
    public BankingCSV.User getBankingUser() {
        return bankingUser;
    }

    // Returns the employee's debit card
    public DebitCard getDebitCard() {
        return debitCard;
    }

    // Returns the employee's credit card
    public CreditCard getCreditCard() {
        return creditCard;
    }


    // -----------------------------
    // Convenience Method
    // -----------------------------
    // A convenience method is a helper method that simplifies tasks

    public void showAccounts() {

        // Print employee information
        System.out.println("Employee: " + name + " | ID: " + employeeID);

        // -----------------------------
        // Show Checking / Savings Accounts
        // -----------------------------
        System.out.println("\n--- Checking & Savings ---");

        // Calls a method inside BankingCSV.User to print account details
        bankingUser.printAccounts();


        // -----------------------------
        // Show Debit Card Info
        // -----------------------------
        System.out.println("\n--- Debit Card ---");

        // Display the debit card fee schedule
        debitCard.displayFeeSchedule();


        // -----------------------------
        // Show Credit Card Info
        // -----------------------------
        System.out.println("\n--- Credit Card ---");

        // Display credit card information
        creditCard.display();
    }
}
