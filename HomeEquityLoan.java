
public class HomeEquityLoan {
    private double appraisedHomeValue; //house value
    private double currentMortgageBalance; //mortgage balance
    private double loanAmount; //total amount loaned
    private double availableEquity; //appraised value - mortgage balance
    
    //fixed interest rate and fees
    private static final double interestRate = 0.03;
    private static final double bankFees = 400.0;
    
    //loan term
    private int termMonths;
    
    //payment tracking
    private double monthlyPayment;
    private double remainingBalance;

    //payment source
    private String accountType;   //"checking" or "savings"

    //progress tracking
    private int paymentsMade;
    private double accountBalance;

    
    //overloaded constructor
    public HomeEquityLoan(double appraisedHomeValue, double currentMortgageBalance, double loanAmount, int termMonths, BankingCSV.User user, String accountID, SavingsAccount savingsAccount, String accounType) {
        this.appraisedValue = appraisedValue;
        this.currentMortgageBalance = currentMortgageBalance;
        this.loanAmount = loanAmount;
        this.termMonths = termMonths;
        
        this.user = user;
        this.accountID = accountID;
        this.savingsAccount = savingsAccount;
        this.accountType = accountType;
        
        this.availableEquity = appraisedValue - currentMortgageBalance;
        
        //fees added to loan balance
        this.remainingBalance = loanAmount + bankFees;
        this.paymentsMade = 0;
    }
    
    //validate eligiblity
    public boolean isApproved() {
        return appraisedValue > 0 && currentMortgageBalance >= 0 && loanAmount > 0 && loanAmount <= availableEquity && termMonths > 0;
    }
    
    //calculate monthly payment
    public void calculateMonthlyPayment() {
        double monthlyRate = interestRate / 12;

        monthlyPayment = (remainingBalance * monthlyRate) / (1 - Math.pow(1 + monthlyRate, -termMonths));
    }
    
    //this is for one payment
    public void makePayment() {
        if (remainingBalance <= 0) 
            System.out.println("Loan already paid off.");
            return;
        

        boolean success = false;

        //CHECKING ACCOUNT
        if (accountType.equalsIgnoreCase("checking")) {

            for (BankingCSV.Account acc : user.accounts) {
                if (acc.accountID.equals(accountID)) {

                    if (acc.balance >= monthlyPayment) {
                        user.withdraw(accountID, monthlyPayment);
                        success = true;
                    
                    break;

        //SAVINGS ACCOUNT
             } else if (accountType.equalsIgnoreCase("savings")) {

                if (savingsAccount != null) 
                    success = savingsAccount.withdrawSavings(monthlyPayment);
             }
        }
            
        //If payment failed
        if (!success) {
            System.out.println("Payment failed: insufficient funds.");
            return;
        }

        //Update loan balance
        double interest = remainingBalance * (interestRate / 12);
        double principal = monthlyPayment - interest;

        remainingBalance -= principal;
        paymentsMade++;

        System.out.println("Loan Payment #" + paymentsMade);
        System.out.println("Interest: " + interest);
        System.out.println("Principal: " + principal);
        System.out.println("Remaining Loan Balance: " + remainingBalance);
    }
    
    //simulate all payments
    public void simulatePayments() {
        while (remainingBalance > 0) {
            makePayment();
        }
        System.out.println("Loan fully paid!");
    }
    
    
    
}