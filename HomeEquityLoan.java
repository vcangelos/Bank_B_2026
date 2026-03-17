
public class HomeEquityLoan {
    private double appraisedHomeValue; //house value
    private double currentMortgageBalance;
    private double loanAmount; //total amount loaned
    private double availableEquity; //appraised value - mortgage balance
    
    //fixed interest rate
    private static final double interestRate;
    
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
}