
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
    private double accountBalance;

    //progress tracking
    private int paymentsMade;
    
    //overloaded constructor
    public HomeEquityLoan(double appraisedHomeValue, double currentMortgageBalance, double loanAmount, int termMonths, String accountType, double accountBalance) {
        this.appraisedValue = appraisedValue;
        this.currentMortgageBalance = currentMortgageBalance;
        this.loanAmount = loanAmount;
        this.termMonths = termMonths;
        this.accountType = accountType;
        this.accountBalance = accountBalance;
        
        this.availableEquity = appraisedValue - currentMortgageBalance;
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
    
    
    
    
    
    
    
    
    
    
}