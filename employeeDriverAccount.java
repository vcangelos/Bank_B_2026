import java.util.ArrayList;
import java.util.List;

public class BankDriver {
    public static void main(String[] args) {

        // Existing users loaded from CSVs
        List<BankingCSV.User> users = new ArrayList<>();

        // Example: create an employee account
        EmployeeAccount emp1 = new EmployeeAccount("223111", "Alice Smith", users);

        // Show their accounts/cards
        emp1.showAccounts();

        // Deposit money into first checking account
        if (!emp1.getBankingUser().accounts.isEmpty()) {
            emp1.getBankingUser().deposit(emp1.getBankingUser().accounts.get(0).accountID, 500.0);
        }

        // Use debit card
        emp1.getDebitCard().deposit(200.0);
        emp1.getDebitCard().withdraw(50.0);

        // Use credit card
        emp1.getCreditCard().addCharge(100.0);
        emp1.getCreditCard().makePayment(25.0);
    }
}
