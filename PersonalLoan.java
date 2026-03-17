import java.time.LocalDate;
import java.util.Random;

public class PersonalLoan {

        private double principal;
        private final double interestRate = 0.20;
        private int loanTerm;
        private String loanType;
        private String loanId;
        private LocalDate startDate;
        private int creditScore;

        public PersonalLoan(double principal, int loanTerm, String loanType, int creditScore) {
                if (principal <= 0) {
                        throw new IllegalArgumentException("Principal must be greater than 0.");
                }
                if (loanTerm <= 0) {
                        throw new IllegalArgumentException("Loan term must be greater than 0.");
                }
                if (loanType == null || loanType.trim().isEmpty()) {
                        throw new IllegalArgumentException("Loan type cannot be blank.");
                }
                if (creditScore < 300 || creditScore > 850) {
                        throw new IllegalArgumentException("Credit score must be between 300 and 850.");
                }

                this.principal = principal;
                this.loanTerm = loanTerm;
                this.loanType = loanType;
                this.loanId = generateLoanId();
                this.startDate = LocalDate.now();
                this.creditScore = creditScore();
        }

        private String generateLoanId() {
                Random random = new Random();
                int number = 10000 + random.nextInt(90000);
                return "LN" + number;
        }

        public double calculateMonthlyPayment() {
                double monthlyRate = interestRate / 12.0;
                return (principal * monthlyRate) /
                        (1 - Math.pow(1 + monthlyRate, -loanTerm));
        }

        public double calculateTotalRepayment() {
                return calculateMonthlyPayment() * loanTerm;
        }

        public void displayLoanInfo() {
                System.out.println("\n=== Personal Loan Details ===");
                System.out.println("Loan ID: " + loanId);
                System.out.println("Loan Type: " + loanType);
                System.out.println("Loan Amount: $" + String.format("%.2f", principal));
                System.out.println("Interest Rate: " + (interestRate * 100) + "%");
                System.out.println("Loan Term: " + loanTerm + " months");
                System.out.println("Start Date: " + startDate);
                System.out.println("Monthly Payment: $" + String.format("%.2f", calculateMonthlyPayment()));
                System.out.println("Total Repayment: $" + String.format("%.2f", calculateTotalRepayment()));
        }
}