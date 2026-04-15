public class StudentLoan {

    private String loanID;
    private double balance;
    private double interestRate;
    private int termMonths;

    public StudentLoan(String loanID, double balance, double interestRate, int termMonths) {
        this.loanID = loanID;
        this.balance = balance;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
    }

    public double getBalance() {sho
        return balance;
    }

    public String getLoanID() {
        return loanID;
    }
}