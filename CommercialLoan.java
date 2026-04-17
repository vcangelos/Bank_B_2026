import java.util.ArrayList;

public class CommercialLoan{
    private double loanAmount;
    private double interestRate;
    private int loanTerm;
    public static ArrayList<CommercialLoan> commercialLoans = new ArrayList<>();
    
    public CommercialLoan(double loanAmount, double interestRate, int loanTerm) {
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.loanTerm = loanTerm;
    }

    public static CommercialLoan create(double loanAmount, double interestRate, int loanTerm) {
        CommercialLoan loan = new CommercialLoan(loanAmount, interestRate, loanTerm);
        commercialLoans.add(loan);
        return loan;
    }

    public double calculateMonthlyPayment() {
        double monthlyInterestRate = interestRate / 12 / 100;
        int numberOfPayments = loanTerm * 12;
        return (loanAmount * monthlyInterestRate) / (1 - Math.pow(1 + monthlyInterestRate, -numberOfPayments));
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public void setLoanTerm(int loanTerm) {
        this.loanTerm = loanTerm;
    }
}