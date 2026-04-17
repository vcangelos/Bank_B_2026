import java.io.IOException;
import java.util.Scanner;

public class LoanDriver {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        CreditCard card = new CreditCard();

        System.out.print("Enter loan amount: ");
        double principal = scanner.nextDouble();

        System.out.print("Enter loan term in months (1-60): ");
        int loanTerm = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter loan type: ");
        String loanType = scanner.nextLine();

        System.out.print("Enable autopay? (yes/no): ");
        String autoPayChoice = scanner.nextLine().trim().toLowerCase();
        boolean autoPayEnabled = autoPayChoice.equals("yes") || autoPayChoice.equals("y");

        PersonalLoan loan = new PersonalLoan(principal, loanTerm, loanType, card, autoPayEnabled);

        CheckingAccount.Account checking = new CheckingAccount.Account("400000000001", 120.00);
        SavingsAccount savings = new SavingsAccount("1001", 300.00);

        loan.display();

        System.out.println("\n--- Attempting autopay ---");
        loan.runAutoPay(checking, savings);

        System.out.println("\nChecking balance: $" + String.format("%.2f", checking.balance));
        System.out.println("Savings balance: $" + String.format("%.2f", savings.getSavings()));

        loan.display();
        loan.saveToCSV();

        scanner.close();
    }
}