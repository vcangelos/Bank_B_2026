import java.util.ArrayList;

public class CommercialLoan{
    private final String loanNumber;
    private double loanAmount;
    private double interestRate = 6.8;
    private int loanTerm;
    public static ArrayList<CommercialLoan> commercialLoans = new ArrayList<>();
    
    public CommercialLoan(double loanAmount, double interestRate, int loanTerm) {
        this.loanNumber = randomLoanNumber();
        this.loanAmount = loanAmount;
        this.loanTerm = loanTerm;
        
    }
    public CommercialLoan(String loanNumber, double loanAmount, double interestRate, int loanTerm) {
        this.loanNumber = loanNumber;
        this.loanAmount = loanAmount;
        this.loanTerm = loanTerm;
    }
    private String randomLoanNumber() {
        long min = 800000000000L;
        long max = 899999999999L;
        long randomNumber = min + (long) (Math.random() * (max - min + 1));
        return String.valueOf(randomNumber);
    }
    public static CommercialLoan createLoan(double loanAmount, double interestRate, int loanTerm) {
        CommercialLoan newLoan = new CommercialLoan(loanAmount, interestRate, loanTerm);
        CommercialLoan.commercialLoans.add(newLoan);
        return newLoan;
    }
    public static CommercialLoan loadLoan(String loanNumber, double loanAmount, double interestRate, int loanTerm) {
        CommercialLoan newLoan = new CommercialLoan(loanNumber, loanAmount, interestRate, loanTerm);
        CommercialLoan.commercialLoans.add(newLoan);
        return newLoan;
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
    public String getLoanNumber() {
        return loanNumber;
    }
    public void makePayment(double paymentAmount) {
        double monthlyPayment = calculateMonthlyPayment();
        if (paymentAmount >= monthlyPayment) {
            loanAmount -= paymentAmount;
            System.out.println("Payment of $" + paymentAmount + " made. Remaining balance: $" + loanAmount);
        } else {
            System.out.println("Payment amount is less than the required monthly payment of $" + monthlyPayment);
        }
    }
}