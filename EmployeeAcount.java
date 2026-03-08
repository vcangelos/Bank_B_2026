import java.util.List;

public class EmployeeAccount {

    private String employeeID;
    private String name;

    // Linked systems
    private BankingCSV.User bankingUser;
    private DebitCard debitCard;
    private CreditCard creditCard;

    // Constructor
    public EmployeeAccount(String employeeID, String name, List<BankingCSV.User> existingUsers) {
        this.employeeID = employeeID;
        this.name = name;

        // --- LINK BANKING USER ---
        // Try to find existing user
        this.bankingUser = BankingCSV.findUser(existingUsers, employeeID);
        if (this.bankingUser == null) {
            // If not found, create new
            this.bankingUser = new BankingCSV.User(employeeID, name);
            existingUsers.add(this.bankingUser);
        }

        // --- LINK DEBIT CARD ---
        this.debitCard = new DebitCard(DebitCard.generateCardNumber(), "1234", employeeID, 
                                       this.bankingUser.accounts.isEmpty() ? "NEW_ACCOUNT" 
                                                                         : this.bankingUser.accounts.get(0).accountID);
        DebitCard.setBankingUsers(existingUsers);

        // --- LINK CREDIT CARD ---
        this.creditCard = new CreditCard();
    }

    // Accessors
    public String getEmployeeID() { return employeeID; }
    public String getName() { return name; }
    public BankingCSV.User getBankingUser() { return bankingUser; }
    public DebitCard getDebitCard() { return debitCard; }
    public CreditCard getCreditCard() { return creditCard; }

    // Convenience methods
    public void showAccounts() {
        System.out.println("Employee: " + name + " | ID: " + employeeID);
        System.out.println("\n--- Checking & Savings ---");
        bankingUser.printAccounts();

        System.out.println("\n--- Debit Card ---");
        debitCard.displayFeeSchedule();

        System.out.println("\n--- Credit Card ---");
        creditCard.display();
    }
}
