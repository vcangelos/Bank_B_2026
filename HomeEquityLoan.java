package Bank;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HomeEquityLoan {

    private double appraisedValue;
    private double currentMortgageBalance;
    private double loanAmount;
    private double availableEquity;

    private static final double interestRate = 0.03;
    private static final double bankFees = 400.0;

    private int termMonths;
    private double monthlyPayment;
    private double remainingBalance;

    private String accountType;
    private int paymentsMade;

    private CheckingAccount.User user;
    private String accountID;
    private CheckingAccount.SavingsAccount savingsAccount;

    public HomeEquityLoan(double appraisedValue,
                          double currentMortgageBalance,
                          double loanAmount,
                          int termMonths,
                          CheckingAccount.User user,
                          String accountID,
                          CheckingAccount.SavingsAccount savingsAccount,
                          String accountType) {

        this.appraisedValue = appraisedValue;
        this.currentMortgageBalance = currentMortgageBalance;
        this.loanAmount = loanAmount;
        this.termMonths = termMonths;

        this.user = user;
        this.accountID = accountID;
        this.savingsAccount = savingsAccount;
        this.accountType = accountType;

        this.availableEquity = appraisedValue - currentMortgageBalance;
        this.remainingBalance = loanAmount + bankFees;
        this.paymentsMade = 0;
    }

    public boolean isApproved() {
        return appraisedValue > 0 &&
               currentMortgageBalance >= 0 &&
               loanAmount > 0 &&
               loanAmount <= availableEquity &&
               termMonths > 0;
    }

    public void calculateMonthlyPayment() {
        double monthlyRate = interestRate / 12;
        monthlyPayment = (remainingBalance * monthlyRate) /
                (1 - Math.pow(1 + monthlyRate, -termMonths));
    }

    public void makePayment() {

        if (remainingBalance <= 0) {
            System.out.println("Loan already paid off.");
            return;
        }

        boolean success = false;

        // CHECKING
        if (accountType.equalsIgnoreCase("checking")) {
            for (CheckingAccount.Account acc : user.accounts) {
                if (acc.accountID.equals(accountID)) {
                    if (acc.balance >= monthlyPayment) {
                        user.withdraw(accountID, monthlyPayment);
                        success = true;
                    }
                    break;
                }
            }
        }

        // SAVINGS
        else if (accountType.equalsIgnoreCase("savings")) {
            if (savingsAccount != null && savingsAccount.balance >= monthlyPayment) {
                savingsAccount.balance -= monthlyPayment;
                success = true;
            }
        }

        if (!success) {
            System.out.println("Payment failed.");
            return;
        }

        double interest = remainingBalance * (interestRate / 12);
        double principal = monthlyPayment - interest;

        remainingBalance -= principal;
        paymentsMade++;

        System.out.println("Payment #" + paymentsMade);
        System.out.println("Remaining Balance: " + remainingBalance);

        saveToCSV();
    }

    public void saveToCSV() {
        try {
            File file = new File("Home.csv");
            boolean fileExists = file.exists();

            FileWriter writer = new FileWriter(file, true);

            if (!fileExists || file.length() == 0) {
                writer.append(
                    "AppraisedValue,CurrentMortgageBalance,LoanAmount,TermMonths,RemainingBalance,PaymentsMade,AccountType\n"
                );
            }

            writer.append(appraisedValue + "," +
                          currentMortgageBalance + "," +
                          loanAmount + "," +
                          termMonths + "," +
                          remainingBalance + "," +
                          paymentsMade + "," +
                          accountType + "\n");

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

