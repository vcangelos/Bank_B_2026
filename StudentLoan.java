import java.util.UUID;
import java.nio.file.*;
import java.io.BufferedReader;

public class StudentLoan {

    private String loanID;
    private double principal;
    private double interestRate;
    private int termMonths;
    private boolean isAutoPayEnabled;
    private CreditCard linkedCard;
    private int creditScore;
    private static final Path StudentLoanCSV = Path.of("StudentLoanList.csv");

    public StudentLoan(String loanID, double principal, double interestRate,
                       int termMonths, CreditCard card, boolean isAutoPayEnabled) {

        this.loanID = loanID;
        this.principal = principal;
        this.interestRate = interestRate;
        this.termMonths = termMonths;
        this.linkedCard = card;
        this.isAutoPayEnabled = isAutoPayEnabled;
        this.creditScore = card.getCreditScore();
    }

    // CREATE LOAN
    public static StudentLoan createStudentLoan(String userID, double amount, CreditCard card) {

        if (amount < 500) {
            System.out.println("Loan must be at least $500");
            return null;
        }

        int score = clampCreditScore(card.getCreditScore());
        double rate = getInterestRate(score);
        


        StudentLoan loan = new StudentLoan(loanID, amount, rate, 120, card, false);

        System.out.println("Loan created for user: " + userID);
        System.out.println("Loan ID: " + loanID);
        System.out.println("Interest Rate: " + (rate * 100) + "%");

        return loan;
    }

    // CLAMP SCORE
    public static int clampCreditScore(int score) {
        if (score < 300) return 300;
        if (score > 850) return 850;
        return score;
    }


    public static String generateUniqueLoanID() {
    String id;

    do {
        id = "LN-" + UUID.randomUUID().toString().substring(0, 6);
    } while (loanExists(id));

    return id;
    }

    public static boolean LoanExists(String loanID){
        //check if there is loans like it return true if there is return false if not.
         try (BufferedReader reader = Files.newBufferedReader(StudentLoanCSV)) {
               reader.readLine(); // this line skips the header for example LoanID
               String line;
               while ((line = reader.readLine()) != null) {
                   String[] currentdata_to_col = line.split(",", -1);
                   if (currentdata_to_col.length > 0 && currentdata_to_col[0].equals(loanId)) {
                       return true;
                   }
               }
        }
    }

    // RATE FROM SCORE
    public static double getInterestRate(int score) {
        if (score >= 750) return 0.03;
        else if (score >= 700) return 0.05;
        else if (score >= 650) return 0.07;
        else if (score >= 600) return 0.10;
        else return 0.15;
    }

    // APPLY INTEREST
    public void applyInterest() {
        principal += principal * interestRate;
    }

    // PAYMENT
    public void makePayment(double amount) {
        principal -= amount;
        if (principal < 0) principal = 0;
    }

    // DISPLAY
    public void display() {
        System.out.println("Loan ID: " + loanID);
        System.out.println("Principal: $" + principal);
        System.out.println("Interest Rate: " + (interestRate * 100) + "%");
        System.out.println("Term: " + termMonths + " months");
        System.out.println("----------------------");
    }
}