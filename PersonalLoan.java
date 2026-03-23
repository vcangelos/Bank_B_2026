import java.time.LocalDate;
import java.util.Random;

public class PersonalLoan {

        // Instance variables
        private double principal;
        private double interestRate;
        private int loanTerm;
        private String loanType;
        private String loanId;
        private LocalDate startDate;
        private int creditScore;
        private boolean approved;
        private CreditCard linkedCard;

        // Constructor
        public PersonalLoan(double principal, int loanTerm, String loanType, CreditCard card) {
                if (principal <= 0) {
                        throw new IllegalArgumentException("Principal must be greater than 0.");
                }
                if (loanTerm <= 0) {
                        throw new IllegalArgumentException("Loan term must be greater than 0.");
                }
                if (loanType == null || loanType.trim().isEmpty()) {
                        throw new IllegalArgumentException("Loan type cannot be blank.");
                }
                if (card == null) {
                        throw new IllegalArgumentException("Credit card cannot be null.");
                }

                this.principal = principal;
                this.loanTerm = loanTerm;
                this.loanType = loanType;
                this.linkedCard = card;
                this.loanId = generateLoanId();
                this.startDate = LocalDate.now();

                // Pull credit score from CreditCard class
                this.creditScore = card.getCreditScore();

                // Decide approval and interest rate
                evaluateLoan();
        }

        // Generates a random loan ID
        private String generateLoanId() {
                Random random = new Random();
                int number = 10000 + random.nextInt(90000);
                return "LN" + number;
        }

        // Evaluates loan approval and assigns realistic interest rate
        private void evaluateLoan() {
                if (creditScore >= 750) {
                        approved = true;
                        interestRate = 0.08; // 8%
                } else if (creditScore >= 700) {
                        approved = true;
                        interestRate = 0.12; // 12%
                } else if (creditScore >= 650) {
                        approved = true;
                        interestRate = 0.16; // 16%
                } else if (creditScore >= 600) {
                        approved = true;
                        interestRate = 0.20; // 20%
                } else {
                        approved = false;
                        interestRate = 0.0;
                }
        }

        // Calculates monthly payment
        public double calculateMonthlyPayment() {
                if (!approved) {
                        return 0.0;
                }

                double monthlyRate = interestRate / 12.0;

                if (monthlyRate == 0) {
                        return principal / loanTerm;
                }

                return (principal * monthlyRate) /
                        (1 - Math.pow(1 + monthlyRate, -loanTerm));
        }

        // Calculates total repayment
        public double calculateTotalRepayment() {
                if (!approved) {
                        return 0.0;
                }
                return calculateMonthlyPayment() * loanTerm;
        }

        // Calculates total interest paid
        public double calculateTotalInterest() {
                if (!approved) {
                        return 0.0;
                }
                return calculateTotalRepayment() - principal;
        }

        // Getters
        public double getPrincipal() {
                return principal;
        }

        public double getInterestRate() {
                return interestRate;
        }

        public int getLoanTerm() {
                return loanTerm;
        }

        public String getLoanType() {
                return loanType;
        }

        public String getLoanId() {
                return loanId;
        }

        public LocalDate getStartDate() {
                return startDate;
        }

        public int getCreditScore() {
                return creditScore;
        }

        public boolean isApproved() {
                return approved;
        }

        public CreditCard getLinkedCard() {
                return linkedCard;
        }

        // Setters
        public void setLoanType(String loanType) {
                if (loanType != null && !loanType.trim().isEmpty()) {
                        this.loanType = loanType;
                }
        }

        public void setLoanTerm(int loanTerm) {
                if (loanTerm > 0) {
                        this.loanTerm = loanTerm;
                }
        }

        public void setPrincipal(double principal) {
                if (principal > 0) {
                        this.principal = principal;
                }
        }

        // Display method
        public void display() {
                System.out.println("\n========== PERSONAL LOAN SUMMARY ==========");
                System.out.println("Loan ID: " + loanId);
                System.out.println("Loan Type: " + loanType);
                System.out.println("Loan Amount: $" + String.format("%.2f", principal));
                System.out.println("Loan Term: " + loanTerm + " months");
                System.out.println("Start Date: " + startDate);
                System.out.println("Linked Card Type: " + linkedCard.getCreditCardType());
                System.out.println("Linked Card Number: " + linkedCard.getFormattedCardNumber());
                System.out.println("Credit Score Used: " + creditScore);

                if (approved) {
                        System.out.println("Loan Status: APPROVED");
                        System.out.println("Interest Rate: " + String.format("%.2f", interestRate * 100) + "%");
                        System.out.println("Monthly Payment: $" + String.format("%.2f", calculateMonthlyPayment()));
                        System.out.println("Total Repayment: $" + String.format("%.2f", calculateTotalRepayment()));
                        System.out.println("Total Interest Paid: $" + String.format("%.2f", calculateTotalInterest()));
                } else {
                        System.out.println("Loan Status: DENIED");
                        System.out.println("Reason: Credit score too low");
                }
        }

        // Optional alias if another class calls displayLoanInfo()
        public void displayLoanInfo() {
                display();
        }
}